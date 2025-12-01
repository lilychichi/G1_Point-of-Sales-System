package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    // --- Configurable Rates ---
    private double vatRate = 0.12;
    private double discountRate = 0.0;
    private double localTaxRate = 0.0;

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("₱#,##0.00");

    // --- FXML UI Elements ---
    @FXML private Label subtotalLabel;
    @FXML private TextField discountInput;
    @FXML private Label discountAmountLabel;
    @FXML private Label vatAmountLabel;
    @FXML private Label taxAmountLabel;
    @FXML private Label grandTotalLabel;
    @FXML private TextField vatRateInput;
    @FXML private TextField taxRateInput;

    @FXML private RadioButton radioCash;
    @FXML private RadioButton radioCard;
    @FXML private ToggleGroup paymentMethodGroup;

    @FXML private VBox cashPaymentPane;
    @FXML private TextField amountPaidField;
    @FXML private Label changeLabel;

    @FXML private VBox cardPaymentPane;
    @FXML private ComboBox<String> cardTypeCombo;
    @FXML private Button processPaymentButton;

    // --- State Variables ---
    private Stage dialogStage;
    private int orderId = -1;
    private List<OrderItem> orderItems;

    private double initialSubtotal = 0.0;
    private double currentDiscountPercent = 0.0;
    private double discountAmount = 0.0;
    private double vatAmount = 0.0;
    private double taxAmount = 0.0;
    private double grandTotal = 0.0;

    private final ObservableList<String> cardTypes = FXCollections.observableArrayList(
            "Gcash", "PayMaya", "Visa", "MasterCard", "Debit Card", "Other Digital"
    );

    public static class OrderItem {
        private final String productId;
        private final String productName;
        private final int quantity;
        private final double price;
        private final double total;

        public OrderItem(String productId, String productName, int quantity, double price, double total) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
        }

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public double getTotal() { return total; }
    }

    public static class PaymentResult {
        public boolean successful = false;
        public int orderId;
        public String paymentType;
        public double finalTotal;
        public double discountAmount;
        public double taxAmount;
        public double vatAmount;
        public double subtotalBeforeDiscount;
        public double amountPaid;
        public double change;
    }

    private PaymentResult result = new PaymentResult();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardTypeCombo.setItems(cardTypes);
        cardTypeCombo.getSelectionModel().selectFirst();

        vatRateInput.setText("12");
        taxRateInput.setText("0");

        handlePaymentMethodChange();

        discountInput.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                discountInput.setText(oldVal);
            }
            recalculateOrderTotals();
        });

        vatRateInput.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                vatRateInput.setText(oldVal);
            }
            recalculateOrderTotals();
        });

        taxRateInput.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                taxRateInput.setText(oldVal);
            }
            recalculateOrderTotals();
        });

        amountPaidField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                amountPaidField.setText(oldVal);
            }
            calculateChange();
        });

        recalculateOrderTotals();
    }

    public void setOrderData(Stage dialogStage, int orderId, double initialSubtotal, List<OrderItem> items) {
        this.dialogStage = dialogStage;
        this.orderId = orderId;
        this.initialSubtotal = initialSubtotal;
        this.orderItems = items;

        discountInput.setText(String.valueOf((int)(discountRate * 100)));
        amountPaidField.setText("0.00");
        // amountPaidField.setText(String.format("%.2f", grandTotal));
        // If you want to automatically set the price.
        recalculateOrderTotals();
        calculateChange();
    }

    private void recalculateOrderTotals() {
        try {
            double inputPercent = Double.parseDouble(discountInput.getText().trim());
            currentDiscountPercent = Math.max(0.0, Math.min(100.0, inputPercent)) / 100.0;
        } catch (NumberFormatException e) {
            currentDiscountPercent = 0.0;
        }

        discountAmount = initialSubtotal * currentDiscountPercent;
        double subtotalAfterDiscount = initialSubtotal - discountAmount;

        try {
            vatRate = Double.parseDouble(vatRateInput.getText().trim()) / 100.0;
        } catch (NumberFormatException e) {
            vatRate = 0.0;
        }

        try {
            localTaxRate = Double.parseDouble(taxRateInput.getText().trim()) / 100.0;
        } catch (NumberFormatException e) {
            localTaxRate = 0.0;
        }

        vatAmount = subtotalAfterDiscount * vatRate;
        taxAmount = subtotalAfterDiscount * localTaxRate;
        grandTotal = subtotalAfterDiscount + vatAmount + taxAmount;

        subtotalLabel.setText(CURRENCY_FORMAT.format(initialSubtotal));
        discountAmountLabel.setText(CURRENCY_FORMAT.format(discountAmount));
        vatAmountLabel.setText(CURRENCY_FORMAT.format(vatAmount));
        taxAmountLabel.setText(CURRENCY_FORMAT.format(taxAmount));
        grandTotalLabel.setText(CURRENCY_FORMAT.format(grandTotal));

        calculateChange();
    }

    @FXML
    private void handlePaymentMethodChange() {
        boolean isCash = radioCash.isSelected();
        cashPaymentPane.setVisible(isCash);
        cashPaymentPane.setManaged(isCash);
        cardPaymentPane.setVisible(!isCash);
        cardPaymentPane.setManaged(!isCash);

        if (isCash) {
            calculateChange();
            amountPaidField.requestFocus();
            amountPaidField.selectAll();
        } else {
            processPaymentButton.setDisable(cardTypeCombo.getSelectionModel().isEmpty());
            cardTypeCombo.requestFocus();
        }
    }

    @FXML
    public void calculateChange() {
        if (!radioCash.isSelected()) return;

        try {
            double amountPaid = Double.parseDouble(amountPaidField.getText());
            double change = amountPaid - grandTotal;

            if (change < 0) {
                changeLabel.setText("Short by " + CURRENCY_FORMAT.format(Math.abs(change)));
                changeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                processPaymentButton.setDisable(true);
            } else {
                changeLabel.setText(CURRENCY_FORMAT.format(change));
                changeLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                processPaymentButton.setDisable(false);
            }
        } catch (NumberFormatException e) {
            changeLabel.setText(CURRENCY_FORMAT.format(0.00));
            changeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            processPaymentButton.setDisable(true);
        }
    }

    @FXML
    private void handleProcessPayment() {
        result.successful = true;
        result.orderId = orderId;
        result.paymentType = radioCash.isSelected() ? "Cash" : "Card/Digital";
        result.finalTotal = grandTotal;
        result.discountAmount = discountAmount;
        result.taxAmount = taxAmount;
        result.vatAmount = vatAmount;
        result.subtotalBeforeDiscount = initialSubtotal;

        try {
            result.amountPaid = Double.parseDouble(amountPaidField.getText());
        } catch (NumberFormatException e) {
            result.amountPaid = 0.0;
        }

        result.change = result.amountPaid - grandTotal;

        dialogStage.close(); // closes the payment window
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public PaymentResult getPaymentResult() {
        return result;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
