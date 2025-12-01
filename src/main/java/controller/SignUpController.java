package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class SignUpController {

    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_fullName;

    @FXML
    private PasswordField pf_password;

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleSignUp(ActionEvent event) {

        String username = tf_username.getText();
        String fullName = tf_fullName.getText();
        String password = pf_password.getText();

        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please fill in all fields (Username, Full Name, Password).");
            return;
        }

        try {
            insertNewUser(username, fullName, password);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully! You can now log in.");
            System.out.println("User '" + username + "' signed up successfully. Switching to Login.");

            // --- FIX APPLIED HERE: Wrap IOException ---
            try {
                switchToLoginScene(event);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load login screen.");
                System.err.println("Error switching scenes: " + e.getMessage());
                e.printStackTrace();
            }
            // ------------------------------------------

        } catch (SQLException e) {

            if (e instanceof SQLIntegrityConstraintViolationException && e.getErrorCode() == 1062) {
                showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "The username '" + username + "' is already taken. Please choose another.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Sign up failed due to a database error. Check console for details.");
                System.err.println("Database Error during sign-up: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void insertNewUser(String username, String fullName, String password) throws SQLException {

        String defaultRole = "admin";

        String insertSQL = "INSERT INTO user (username, fullName, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, username);
            pstmt.setString(2, fullName);
            pstmt.setString(3, password);
            pstmt.setString(4, defaultRole);

            pstmt.executeUpdate();
        }
    }

    @FXML
    public void switchToLoginScene(ActionEvent event) throws IOException {
        System.out.println("Switching to Login screen...");

        Parent root = FXMLLoader.load(getClass().getResource("/controller/controller.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("QTen POS Login");
        stage.show();
    }
}