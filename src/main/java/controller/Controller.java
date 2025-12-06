package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextField tf_username;

    @FXML
    private PasswordField pf_password;

    @FXML
    private Button button_login;

    @FXML
    private Button button_signup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            if (tf_username != null) {
                tf_username.requestFocus();
                System.out.println("Initial focus set on Username field.");
            }
        });
    }

    @FXML
    public void loginButtonAction(ActionEvent event) {
        String username = tf_username.getText();
        String password = pf_password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            // Show visual warning for empty fields
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter both username and password.");
            return;
        }

        System.out.println("Attempting login for: " + username);

        if (validateLogin(username, password)) {
            System.out.println("Login Successful for user: " + username);
            try {
                switchToLoggedInScene(event);
            } catch (IOException e) {
                System.err.println("Error switching to logged-in scene.");
                e.printStackTrace();
            }
        } else {
            // Show visual error for wrong credentials
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateLogin(String username, String password) {
        String query = "SELECT COUNT(*) FROM user WHERE username = ? AND password = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during login validation.");
            e.printStackTrace();
            // Optional: Show an alert if the database connection fails entirely
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to the database.");
            return false;
        }
        return false;
    }

    public void switchToLoggedInScene(ActionEvent event) throws IOException {
        System.out.println("Switching to Logged-in screen...");
        Parent root = FXMLLoader.load(getClass().getResource("logged-in.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("QTen POS - Dashboard");
        stage.show();
    }

    @FXML
    public void signUpButtonAction(ActionEvent event) throws IOException {
        System.out.println("Switching to Sign Up screen...");
        Parent root = FXMLLoader.load(getClass().getResource("sign-up.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("QTen POS Sign Up");
        stage.show();
    }
}