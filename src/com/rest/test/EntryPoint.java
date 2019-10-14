package com.rest.test;


import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/entry-point")
public class EntryPoint {

    @GET
    @Path("getAccountInfo/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAccountInfo(@PathParam("id") String id) {
        
        int accountID = 0;
        int accountFunds = 0;
        
        if(id.isEmpty()) {
            return "Missing account ID";
        }
        try {
            accountID = Integer.parseInt(id);
            
            Set<Entry<Integer,Integer>> accountRes = H2Select.accountInfo(accountID).entrySet();
            if(!accountRes.isEmpty()) {
                Entry<Integer,Integer> account = accountRes.iterator().next();
                accountFunds = account.getValue();
            }
            else {
                return "No account with id : "+accountID+" found.";
            }
            
        }
        catch(NumberFormatException e) {
            return "Account ID must be numeric";
        }
        
        return "Account : "+accountID+" | Funds: "+accountFunds;
    }
    
    @GET
    @Path("showAllAccounts")
    @Produces(MediaType.TEXT_PLAIN)
    public String showAllAccounts() {
        

            StringBuffer res = new StringBuffer();
            Set<Entry<Integer,Integer>> accounts = H2Select.allAccounts().entrySet();
            if(!accounts.isEmpty()) {
                for (Iterator<Entry<Integer,Integer>> iterator = accounts.iterator(); iterator.hasNext();) {
                    Entry<Integer, Integer> account = (Entry<Integer, Integer>) iterator.next();
                    
                    res.append("Account: "+account.getKey()+" , funds : "+account.getValue()+"\n");
                }
            }
            

        return res.toString();
    }
    
    
    
    @POST
    @Path("createAccount")
    @Produces(MediaType.TEXT_PLAIN)
    public String createAccount(@FormParam("funds") String funds){
        
        int accountId = 0;
        try {
            accountId = H2Insert.insertRecord(Math.abs(Integer.parseInt(funds)));
        }
        catch(SQLException e) {
            return "Error inserting account into table";
        }
        catch(NumberFormatException e) {
            if(funds.isEmpty()) {
                return "Account not created. No value for funds.";
            }
            return "Account not created. Funds must be numeric.";
        }
        
        return "Account with ID : "+accountId+" created! Total funds in account: "+funds;
    }
    
    @POST
    @Path("removeAccount/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String removeAccount(@PathParam("id") String id){
        
        int accountID = 0;
        if(id.isEmpty()) {
            return "Missing accountID to be removed.";
        }
        try {
            accountID = Integer.parseInt(id);
            H2Remove.removeAccountId(accountID);
        }
        catch(NumberFormatException nfe) {
            return "Account ID must be numeric";
        }
        catch(SQLException se) {
            return "Error removing account from table";
        }
        
        return "Account : "+accountID+" removed.";
    }
    
    @POST
    @Path("transferMoney")
    @Produces(MediaType.TEXT_PLAIN)
    public String transferMoney(@FormParam("account1") String account1,@FormParam("account2") String account2,@FormParam("funds") String funds) throws SQLException{
        
        int fundsTransferred = 0;
        int fundsAccount1 = 0;
        int fundsAccount2 = 0;
        
        if(funds.isEmpty()) {
            return "No value for funds.";
        }
        if(account1.isEmpty()) {
            return "Missing ID for account1";
        }
        if(account2.isEmpty()) {
            return "Missing ID for account2";
        }
        try {
            fundsTransferred = Math.abs(Integer.parseInt(funds));

            Set<Entry<Integer,Integer>> account1InfoSet = H2Select.accountInfo(Integer.parseInt(account1)).entrySet();
            
            //get account1 funds
            if(!account1InfoSet.isEmpty()) {
                Entry<Integer,Integer> account = account1InfoSet.iterator().next();
                fundsAccount1 = account.getValue();
            }
        
            if(fundsTransferred > fundsAccount1) {
                return "Not enough funds in account : "+account1;
            }
            else {
                
                Set<Entry<Integer,Integer>> account2InfoSet = H2Select.accountInfo(Integer.parseInt(account2)).entrySet();
                //get account2 funds
                if(!account2InfoSet.isEmpty()) {
                    Entry<Integer,Integer> account = account2InfoSet.iterator().next();
                    fundsAccount2 = account.getValue();
                }
                
                H2Update.updateAccount(Integer.parseInt(account2),fundsAccount2+fundsTransferred);
                H2Update.updateAccount(Integer.parseInt(account1),fundsAccount1-fundsTransferred);
            }
        }
        catch(NumberFormatException e) {
            return "Funds must be numeric.";
        }
        
        return  fundsTransferred + " have been transferred to: "+account2;
    }
}