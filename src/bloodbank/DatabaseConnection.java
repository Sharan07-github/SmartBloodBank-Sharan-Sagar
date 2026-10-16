package bloodbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection class handles Oracle database connectivity
 * Uses Singleton pattern to ensure single connection instance
 */
public class DatabaseConnection {
    
    // Database connection parameters - UPDATE THESE WITH YOUR DATABASE DETAILS
    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/orcl123";
    private static final String DB_USERNAME = "BLOODBANK";  
    private static final String DB_PASSWORD = "BLOOD143";  
    
    private static Connection connection = null;
    
    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConnection() {}
    
    /**
     * Get database connection using Singleton pattern
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load Oracle JDBC Driver
                Class.forName("oracle.jdbc.driver.OracleDriver");
                
                // Establish connection
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                System.out.println("Database connected successfully!");
                
            } catch (ClassNotFoundException e) {
                System.err.println("Oracle JDBC Driver not found!");
                System.err.println("Make sure ojdbc8.jar is in your classpath");
                throw new SQLException("Driver not found", e);
            } catch (SQLException e) {
                System.err.println("Database connection failed!");
                System.err.println("Please check your database connection details:");
                System.err.println("URL: " + DB_URL);
                System.err.println("Username: " + DB_USERNAME);
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            Connection testConn = getConnection();
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}