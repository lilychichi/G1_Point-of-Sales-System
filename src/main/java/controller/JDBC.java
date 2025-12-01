package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class JDBC {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver class not found. Check if the MySQL dependency is correctly loaded by Maven/Module-Info.");
            throw new SQLException("MySQL Driver not available in the application runtime. Configuration issue.", e);
        }

        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void main(String[] args) {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from user");

            while (resultSet.next()) {
                System.out.println(resultSet.getString("fullName"));
            }

            resultSet.close();
            statement.close();
            connection.close();

            System.out.println("Connection successful.");

        } catch (SQLException e) {
            System.err.println("Database connection or query failed. Check credentials and server status.");
            e.printStackTrace();
        }
    }
}
