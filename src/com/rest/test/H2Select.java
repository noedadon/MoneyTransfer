package com.rest.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Select PreparedStatement JDBC Example
 * Read record in table
 *
 */
public class H2Select {
    private static final String QUERY = "select id,funds from account where id =?";
    private static final String QUERY2 = "select * from account";

    public static HashMap<Integer,Integer> accountInfo(int id) {

        HashMap<Integer,Integer> account = new HashMap<>();
        
        // Step 1: Establishing a Connection
        try (Connection connection = H2JDBCUtils.getConnection();

            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(QUERY);) {
            preparedStatement.setInt(1, id);
            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            if(rs.next()) {
                int idFound = rs.getInt("id");
                int funds = rs.getInt("funds");

                account.put(idFound, funds);
            }
        } catch (SQLException e) {
            H2JDBCUtils.printSQLException(e);
        }
        // Step 4: try-with-resource statement will auto close the connection.
        
        return account;
    }
    
    public static HashMap<Integer,Integer> allAccounts() {

        HashMap<Integer,Integer> accounts = new HashMap<>();
        
        // Step 1: Establishing a Connection
        try (Connection connection = H2JDBCUtils.getConnection();

            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(QUERY2);) {
            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while(rs.next()) {
                int idFound = rs.getInt("id");
                int funds = rs.getInt("funds");

                accounts.put(idFound, funds);
            }
        } catch (SQLException e) {
            H2JDBCUtils.printSQLException(e);
        }
        // Step 4: try-with-resource statement will auto close the connection.
        
        return accounts;
    }
}
