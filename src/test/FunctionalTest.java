package test;

import com.rest.test.App;
import com.rest.test.EntryPoint;
import com.rest.test.H2Create;
import com.rest.test.H2Insert;
import com.rest.test.H2Remove;
import com.rest.test.H2Select;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import io.restassured.response.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class FunctionalTest {
    
    private static URI serverUri;
    private static Server jettyServer;
    private static String entryPoint = "/entry-point";
    private static Set<Integer> accountsToBeDeleted;
    
    @BeforeClass
    public static void startJetty() throws Exception
    {
        accountsToBeDeleted = new HashSet<>();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        jettyServer = new Server(8080);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
             org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
           "jersey.config.server.provider.classnames",
           EntryPoint.class.getCanonicalName());

        H2Create.createTable();
        
        jettyServer.start();

        // Determine Base URI for Server
        String host = "localhost";
        int port = 8080;
        serverUri = new URI(String.format("http://%s:%d/",host,port));
    }

    @AfterClass
    public static void stopJetty()
    {
        if(!accountsToBeDeleted.isEmpty()) {
            Iterator<Integer> accsIterator = accountsToBeDeleted.iterator();
            while(accsIterator.hasNext()) {
                try {
                    H2Remove.removeAccountId(accsIterator.next());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        try
        {
            jettyServer.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetNonExistentAccountID() throws Exception
    {
        
        String id = "0";
        String response = get(entryPoint+"/getAccountInfo/"+id).asString();
        assertEquals(response,"No account with id : "+id+" found.");

    }
    
    @Test
    public void testGetExistentAccountID() throws Exception
    {
        //Create an account, try to retrieve it and remove it once tests are all completed
        int accountFunds = 200;
        int accountID = H2Insert.insertRecord(accountFunds);
        String response = get(entryPoint+"/getAccountInfo/"+accountID).asString();
        accountsToBeDeleted.add(accountID);
        assertEquals(response,"Account : "+accountID+" | Funds: "+accountFunds);
    }
    
    @Test
    public void testShowAllAccounts() throws Exception
    {
        //We assume there are accounts in the table.
        String response = get(entryPoint+"/showAllAccounts/").asString();
        assertTrue(!response.isEmpty());
    }
    
    @Test
    public void testCreateAnAccount() throws Exception
    {
        int randomFunds = 200;
        String response = post(entryPoint+"/createAccount?funds="+randomFunds).asString();

        String accountID = response.substring(response.indexOf(":")+1,response.indexOf("created")).trim();
        int accountID_int = Integer.parseInt(accountID);
        accountsToBeDeleted.add(accountID_int);
        assertTrue(H2Select.accountInfo(accountID_int).containsKey(accountID_int));
    }
    
    @Test
    public void testRemoveAnAccount() throws Exception
    {
        int accountFunds = 200;
        int accountID = H2Insert.insertRecord(accountFunds);
        String response = post(entryPoint+"/removeAccount/"+accountID).asString();

        accountsToBeDeleted.add(accountID);
        assertEquals(response,"Account : "+accountID+" removed.");
        assertTrue(H2Select.accountInfo(accountID).isEmpty());
    }
    
    @Test
    public void testTransferMoneyBetween2accounts_firstPositiveNonZeroBalance() throws Exception
    {
        int fundsTransferred = 50;
        int accountFunds = 200;
        int account1ID = H2Insert.insertRecord(accountFunds);
        int account1ID_prevFunds = H2Select.accountInfo(account1ID).entrySet().iterator().next().getValue();
        
        int accountFunds2 = 100;
        int account2ID = H2Insert.insertRecord(accountFunds2);
        int account2ID_prevFunds = H2Select.accountInfo(account2ID).entrySet().iterator().next().getValue();
        
        String response = post(entryPoint+"/transferMoney?account1="+account1ID+"&account2="+account2ID+"&funds="+fundsTransferred).asString();
        
        accountsToBeDeleted.add(account1ID);
        accountsToBeDeleted.add(account2ID);
        
        int account2ID_afterTransfer = H2Select.accountInfo(account2ID).entrySet().iterator().next().getValue();
        int account1ID_afterTransfer = H2Select.accountInfo(account1ID).entrySet().iterator().next().getValue();
        
        assertEquals(new BigDecimal(account2ID_afterTransfer).subtract(new BigDecimal(fundsTransferred)),
                     new BigDecimal(account2ID_prevFunds));
        
        assertEquals(new BigDecimal(account1ID_afterTransfer),
                     new BigDecimal(account1ID_prevFunds).subtract(new BigDecimal(fundsTransferred)));
        
        assertEquals(response,fundsTransferred + " have been transferred to: "+account2ID);
        
        
    }
    
    @Test
    public void testTransferMoneyBetween2accounts_firstNotEnoughBalance() throws Exception
    {
        int fundsTransferred = 52;
        int accountFunds = 50;
        int account1ID = H2Insert.insertRecord(accountFunds);
        int account1ID_prevFunds = H2Select.accountInfo(account1ID).entrySet().iterator().next().getValue();
        
        int accountFunds2 = 100;
        int account2ID = H2Insert.insertRecord(accountFunds2);
        int account2ID_prevFunds = H2Select.accountInfo(account2ID).entrySet().iterator().next().getValue();
        
        String response = post(entryPoint+"/transferMoney?account1="+account1ID+"&account2="+account2ID+"&funds="+fundsTransferred).asString();
        
        accountsToBeDeleted.add(account1ID);
        accountsToBeDeleted.add(account2ID);
        
        int account2ID_afterTransfer = H2Select.accountInfo(account2ID).entrySet().iterator().next().getValue();
        int account1ID_afterTransfer = H2Select.accountInfo(account1ID).entrySet().iterator().next().getValue();
        
        assertEquals(response,"Not enough funds in account : "+account1ID);
        
        assertEquals(new BigDecimal(account1ID_prevFunds),new BigDecimal(account1ID_afterTransfer));
        assertEquals(new BigDecimal(account2ID_prevFunds),new BigDecimal(account2ID_afterTransfer));
 
    }
}
