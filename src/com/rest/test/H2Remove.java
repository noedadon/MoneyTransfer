package com.rest.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Update PreparedStatement JDBC Example
 *
 */
public class H2Remove {

    private static final String REMOVE_ACCOUNT_SQL = "delete from account where id = ?;";



    public static void removeAccountId(int id) throws SQLException {

        // Step 1: Establishing a Connection
        try (Connection connection = H2JDBCUtils.getConnection();
            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_ACCOUNT_SQL)) {
            preparedStatement.setInt(1, id);

            // Step 3: Execute the query or update query
            preparedStatement.execute();
        } catch (SQLException e) {

            // print SQL exception information
            H2JDBCUtils.printSQLException(e);
        }

        // Step 4: try-with-resource statement will auto close the connection.
    }
}
