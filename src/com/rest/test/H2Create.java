package com.rest.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Create Statement JDBC Example
 *
 */
public class H2Create {

    private static final String createTableSQL = "CREATE TABLE IF NOT EXISTS account (\r\n" + "  id  int(3) primary key,\r\n" +
        "  funds int(4),\r\n" + "  );";



    public static void createTable() throws SQLException {

        // Step 1: Establishing a Connection
        try (Connection connection = H2JDBCUtils.getConnection();
            // Step 2:Create a statement using connection object
            Statement statement = connection.createStatement();) {

            // Step 3: Execute the query or update query
            statement.execute(createTableSQL);

        } catch (SQLException e) {
            // print SQL exception information
            H2JDBCUtils.printSQLException(e);
        }
    }
}