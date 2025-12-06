package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML private TextField tf_username;
    @FXML private TextField tf_lastName;
    @FXML private TextField tf_firstName;
    @FXML private TextField tf_middleName;
    @FXML private PasswordField pf_password;
    @FXML private ChoiceBox<String> cb_role;

    private static final String NAME_VALIDATION_REGEX = ".*\\D.*";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (cb_role != null) {
            cb_role.setItems(FXCollections.observableArrayList("Admin", "Cashier"));
            cb_role.setValue("Cashier");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleSignUp(ActionEvent event) {
        String username = tf_username.getText().trim();
        String lastName = tf_lastName.getText().trim();
        String firstName = tf_firstName.getText().trim();
        String middleName = tf_middleName.getText().trim();
        String password = pf_password.getText();
        String role = cb_role.getValue();

        if (username.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || password.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please fill in all required fields (including Role selection).");
            return;
        }

        if (!lastName.matches(NAME_VALIDATION_REGEX) || !firstName.matches(NAME_VALIDATION_REGEX)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Name Format", "Last Name and First Name must contain letters and cannot be purely numeric.");
            return;
        }

        if (!middleName.isEmpty() && !middleName.matches(NAME_VALIDATION_REGEX)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Name Format", "Middle Name cannot be purely numeric.");
            return;
        }

        try {
            insertNewUser(username, lastName, firstName, middleName, password, role);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully! You can now log in.");
            System.out.println("User '" + username + "' signed up successfully. Role: " + role);

            try {
                switchToLoginScene(event);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load login screen.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException && e.getErrorCode() == 1062) {
                showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "The username '" + username + "' is already taken. Please choose another.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Sign up failed due to a database error.\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void insertNewUser(String username, String lastName, String firstName, String middleName, String password, String role) throws SQLException {
        String insertSQL = "INSERT INTO user (username, lastName, firstName, middleName, password, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, username);
            pstmt.setString(2, lastName);
            pstmt.setString(3, firstName);

            if (middleName == null || middleName.isEmpty()) {
                pstmt.setNull(4, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(4, middleName);
            }

            pstmt.setString(5, password);
            pstmt.setString(6, role);

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