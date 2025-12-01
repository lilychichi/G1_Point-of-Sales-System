package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController {


    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_fullName;

    @FXML
    private PasswordField pf_password;

    /**
     * Handles the "Sign Up" button click (onAction="#handleSignUp").
     */
    @FXML
    public void handleSignUp(ActionEvent event) {

        String username = tf_username.getText();
        String fullName = tf_fullName.getText();
        String password = pf_password.getText();

        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            System.err.println("Error: Please fill in all required fields (Username, Full Name, Password).");
            return;
        }

        boolean success = insertNewUser(username, fullName, password);

        if (success) {
            System.out.println("User '" + username + "' signed up successfully. Switching to Login.");
            try {
                switchToLoginScene(event);
            } catch (IOException e) {
                System.err.println("Error switching scenes after successful sign up.");
                e.printStackTrace();
            }
        } else {
            System.err.println("Sign Up Failed. Check database configuration or console logs for details.");
        }
    }

    /**
     * Helper method to insert user data into the 'User' table.
     * Inserts into: username, fullname, password, role.
     * @return true if one row was affected (insertion successful).
     */
    private boolean insertNewUser(String username, String fullName, String password) {

        String defaultRole = "admin";

        String insertSQL = "INSERT INTO user (username, fullName, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {


            pstmt.setString(1, username);   // Maps to username
            pstmt.setString(2, fullName);   // Maps to fullName
            pstmt.setString(3, password);   // Maps to password
            pstmt.setString(4, defaultRole); // Maps to role

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database Error during sign-up attempt:");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());

            if (e.getErrorCode() == 1062) {
                System.err.println("Error Message: Duplication error. The chosen username may already exist.");
            } else {
                System.err.println("Detailed SQL Message: " + e.getMessage());
            }
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Switches the current scene to the Login page (controller.fxml).
     */
    @FXML
    public void switchToLoginScene(ActionEvent event) throws IOException {
        System.out.println("Switching to Login screen...");

        Parent root = FXMLLoader.load(getClass().getResource("controller.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("QTen POS Login");
        stage.show();
    }
}