package com.rest.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Insert PrepareStatement JDBC Example
 * 
 *
 */
public class H2Insert {
    private static final String INSERT_USERS_SQL = "INSERT INTO account" +
        "  (id, funds) VALUES " +
        " (?, ?);";



    public static int insertRecord(int funds) throws SQLException {
        
        int randomId = 0;
        // Step 1: Establishing a Connection
        try (Connection connection = H2JDBCUtils.getConnection();
            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            
            randomId = (int)(Math.random()*9000)+1000;
            preparedStatement.setInt(1,randomId);
            preparedStatement.setInt(2,Math.abs(funds));

            // Step 3: Execute the query or update query
            preparedStatement.executeUpdate();
            
        } 
        catch (SQLException e) {

            // print SQL exception information
            H2JDBCUtils.printSQLException(e);
        }
        
        return randomId;
        
        // Step 4: try-with-resource statement will auto close the connection.
    }
}