package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class QuantityController {

    @FXML
    private TextField quantityField;

    @FXML
    private Label productLabel;

    @FXML
    private Label priceLabel;

    private Stage dialogStage;
    private String productId;
    private double price;
    private int enteredQuantity = 0;

    /**
     * Initializes the controller elements.
     * Ensures only numeric input is accepted in the quantity field.
     */
    @FXML
    private void initialize() {
        // Enforce numeric input (only digits)
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantityField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void setProductInfo(String productId, String productName, double price) {
        this.productId = productId;
        this.price = price;
        // Set the labels with the passed product information
        this.productLabel.setText("Product: " + productName);
        this.priceLabel.setText("Price: ₱" + String.format("%.2f", price));
        // Set initial quantity to 1 and select it for easy overwrite
        this.quantityField.setText("1");
        this.quantityField.selectAll();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Returns the quantity entered by the user.
     * @return The quantity, or 0 if cancelled or invalid.
     */
    public int getEnteredQuantity() {
        return enteredQuantity;
    }

    /**
     * Handles the "Add to Order" button click (onAction="#handleAdd" in FXML).
     */
    @FXML
    private void handleAdd() {
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity > 0) {
                this.enteredQuantity = quantity;
                if (dialogStage != null) {
                    dialogStage.close();
                }
            } else {
                System.err.println("Quantity must be a positive number.");
                // Show a visual error message to the user here
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Please enter a whole number for quantity.");
            // Show a visual error message to the user here
        }
    }

    /**
     * Handles the "Cancel" button click (onAction="#handleCancel" in FXML).
     */
    @FXML
    private void handleCancel() {
        this.enteredQuantity = 0; // Set to 0 to indicate cancellation
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    // Getter for ProductId (needed by LoggedInController if it needs to retrieve the ID)
    public String getProductId() {
        return productId;
    }
}
