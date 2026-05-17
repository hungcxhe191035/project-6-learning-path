package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

public class DBContext {
    public static Connection getConnection() throws Exception {
        ResourceBundle rb = ResourceBundle.getBundle("config.db");
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(rb.getString("db.url"), rb.getString("db.user"), rb.getString("db.password"));
    }

    public static void main(String[] args) {
        try {
            System.out.println("Connecting to SQL Server...");
            Connection conn = getConnection();
            
            if (conn != null) {
                System.out.println("DATABASE CONNECTION SUCCESSFUL!");
                System.out.println("Connection info: " + conn.getMetaData().getDatabaseProductName() + " " + conn.getMetaData().getDatabaseProductVersion());
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("DATABASE CONNECTION FAILED!");
            e.printStackTrace();
        }
    }
}