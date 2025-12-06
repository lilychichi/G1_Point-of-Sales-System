package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import controller.PdfGenerator;

public class LoggedInController implements Initializable {

    @FXML private Button button_logout;
    @FXML private Label welcome;
    @FXML private Button button_manage;
    @FXML private Button button_product_manage;
    @FXML private Button button_new_order;
    @FXML private Button button_payment;
    @FXML private Button button_cancel_order;
    @FXML private Button button_sales_report;
    @FXML private Button button_display;
    @FXML private Button button_com;
    @FXML private Button button_audio;
    @FXML private Button button_cables;
    @FXML private AnchorPane maindash;
    @FXML private Label orderID;
    @FXML private Label orderDate;
    @FXML private TextArea receiptTextArea;
    @FXML private Label receiptTransactionIdLabel;

    @FXML private TableView<OrderItem> orderview;
    @FXML private TableColumn<OrderItem, String> viewName;
    @FXML private TableColumn<OrderItem, Double> viewPrice;
    @FXML private TableColumn<OrderItem, Integer> viewQuantity;
    @FXML private TableColumn<OrderItem, Double> viewTotal;
    @FXML private Label totalPrice;
    @FXML private TextField taxRateField;
    @FXML private TextField discountRateField;

    private final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private int currentOrderId = -1;
    private String currentOrderDate = "";
    private final StringBuilder barcodeBuffer = new StringBuilder();
    private long lastKeyTime = 0;
    private static final long BARCODE_TIMEOUT = 50;

    private static final String IMAGE_STORAGE_DIR = "product_images";

    // Hardcoded User ID (Admin) - In a real app, pass this from Login
    private int currentUserId = 1;

    public static class ProductDisplay {
        private final int id;
        private final String name;
        private final double price;
        private final String imagePath;
        private final String barcode;
        public ProductDisplay(int id, String name, double price, String imagePath, String barcode) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.imagePath = imagePath;
            this.barcode = barcode;
        }
        public int getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getImagePath() { return imagePath; }
        public String getBarcode() { return barcode; }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupOrderTable();
        if (orderID != null) orderID.setText("Order ID: N/A");
        if (orderDate != null) orderDate.setText("Date: N/A");
        setCategoryButtonsDisabled(true);
        Platform.runLater(this::addGlobalKeyListener);
    }

    private void updateTotalPrice() {
        double total = orderItems.stream().mapToDouble(OrderItem::getTotal).sum();
        String formattedTotal = String.format("₱%.2f", total);
        totalPrice.setText(formattedTotal);
    }

    private void handleEditQuantity(OrderItem itemToEdit) {
        openQuantityDialog(itemToEdit.getProductId(), itemToEdit.getName(), itemToEdit.getPrice());
    }

    private void handleDelete(OrderItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Remove Item from Order?");
        alert.setContentText("Are you sure you want to remove " + item.getName() + " from the order?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            orderItems.remove(item);
            showAlert(Alert.AlertType.INFORMATION, "Item Removed", item.getName() + " has been removed.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateOrderLabels() {
        if (currentOrderId != -1) {
            if (orderID != null) orderID.setText(String.format("Order ID: %04d", currentOrderId));
            if (orderDate != null) orderDate.setText("Date: " + currentOrderDate);
        } else {
            if (orderID != null) orderID.setText("Order ID: N/A");
            if (orderDate != null) orderDate.setText("Date: N/A");
        }
    }

    private void setCategoryButtonsDisabled(boolean disabled) {
        if (button_display != null) button_display.setDisable(disabled);
        if (button_com != null) button_com.setDisable(disabled);
        if (button_audio != null) button_audio.setDisable(disabled);
        if (button_cables != null) button_cables.setDisable(disabled);
    }

    private Stage openModalWindow (String resource, String title) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/" + resource));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setAlwaysOnTop(true);
        stage.setIconified(false);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle(title);
        stage.showAndWait();
        return stage;
    }

    // --- PERSISTENCE METHODS FOR DIRECT FOREIGN KEYS ---

    private boolean saveFullOrderTransaction(int orderId, List<OrderItem> items) {
        try (Connection conn = JDBC.getConnection()) {
            conn.setAutoCommit(false);

            // Matches your Schema: Inserting into orderdetails with Foreign Keys directly.
            // We use a SUBQUERY to get the category_idCategory from the product table based on the product ID.
            String insertOD = "INSERT INTO orderdetails " +
                    "(quantity, price, subtotal, discount, vat, tax, warranty, order_idOrder, order_user_idUser, product_idProduct, product_category_idCategory) " +
                    "VALUES (?, ?, ?, 0.0, 0.0, 0.0, NULL, ?, ?, ?, (SELECT category_idCategory FROM product WHERE idProduct = ?))";

            try (PreparedStatement psOD = conn.prepareStatement(insertOD)) {
                for (OrderItem item : items) {
                    psOD.setInt(1, item.getQuantity());
                    psOD.setDouble(2, item.getPrice());
                    psOD.setDouble(3, item.getTotal());
                    psOD.setInt(4, orderId); // order_idOrder
                    psOD.setInt(5, currentUserId); // order_user_idUser
                    psOD.setInt(6, Integer.parseInt(item.getProductId())); // product_idProduct
                    psOD.setInt(7, Integer.parseInt(item.getProductId())); // product_idProduct (Used in subquery)

                    psOD.addBatch();
                }
                psOD.executeBatch();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Transaction failed (Order Details): " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean updateInventory(List<OrderItem> items) {
        String updateQuery = "UPDATE Product SET StockQuantity = StockQuantity - ? WHERE idProduct = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {
            for (OrderItem item : items) {
                ps.setInt(1, item.getQuantity());
                ps.setInt(2, Integer.parseInt(item.getProductId()));
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean savePaymentLog(int orderId, double amountPaid, double change) {
        // Matches your Schema: Payment table has order_idOrder and order_user_idUser
        String insertPayment = "INSERT INTO payment (payment_type, payment_Date, amountPaid, `change`, order_idOrder, order_user_idUser) VALUES (?, ?, ?, ?, ?, ?)";
        String updateOrderTotal = "UPDATE `order` SET totalAmount = ? WHERE idOrder = ?";

        try (Connection conn = JDBC.getConnection()) {
            conn.setAutoCommit(false);

            String dateTimeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"));

            try (PreparedStatement psPay = conn.prepareStatement(insertPayment)) {
                psPay.setString(1, "Cash");
                psPay.setString(2, dateTimeNow);
                psPay.setDouble(3, amountPaid);
                psPay.setDouble(4, change);
                psPay.setInt(5, orderId); // order_idOrder
                psPay.setInt(6, currentUserId); // order_user_idUser
                psPay.executeUpdate();
            }

            try (PreparedStatement psOrder = conn.prepareStatement(updateOrderTotal)) {
                double total = amountPaid - change;
                psOrder.setDouble(1, total);
                psOrder.setInt(2, orderId);
                psOrder.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Payment log failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void setupOrderTable() {
        viewName.setCellValueFactory(new PropertyValueFactory<>("name"));
        viewPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        viewQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        viewTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        TableColumn<OrderItem, Void> deleteCol = new TableColumn<>("");
        deleteCol.setPrefWidth(70);
        deleteCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px;");
                deleteButton.setOnAction(event -> handleDelete(getTableView().getItems().get(getIndex())));
            }
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
        orderview.getColumns().add(deleteCol);
        orderview.setItems(orderItems);
        orderview.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && orderview.getSelectionModel().getSelectedItem() != null) {
                handleEditQuantity(orderview.getSelectionModel().getSelectedItem());
            }
        });
        orderItems.addListener((javafx.collections.ListChangeListener<OrderItem>) c -> {
            while (c.next()) if (c.wasAdded() || c.wasRemoved() || c.wasUpdated()) updateTotalPrice();
        });
    }

    private void startNewOrderTransaction(boolean confirm) {
        if (confirm && currentOrderId != -1 && !orderItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Start a New Order? Current items will be cleared.", ButtonType.OK, ButtonType.CANCEL);
            if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        }

        orderItems.clear();
        LocalDate today = LocalDate.now();
        String dateString = today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        currentOrderDate = dateString;

        // Matches your Schema: Order table has user_idUser
        String query = "INSERT INTO `Order` (OrderDate, totalAmount, user_idUser) VALUES (?, ?, ?)";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dateString);
            ps.setDouble(2, 0.00);
            ps.setInt(3, currentUserId); // user_idUser

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) currentOrderId = generatedKeys.getInt(1);
            }
            updateOrderLabels();
            setCategoryButtonsDisabled(false);
            System.out.println("New Order ID: " + currentOrderId);
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create order: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private ProductDisplay getProductByBarcode(String barcode) {
        // Updated Query: Joins with category to match your new schema
        String query = "SELECT p.idProduct, p.product_name, p.price, p.image_path, p.barcode " +
                "FROM product p JOIN category c ON p.category_idCategory = c.idCategory " +
                "WHERE p.barcode = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new ProductDisplay(rs.getInt("idProduct"), rs.getString("product_name"), rs.getDouble("price"), rs.getString("image_path"), rs.getString("barcode"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private int getProductStock(int productId) {
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT StockQuantity FROM Product WHERE idProduct = ?")) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("StockQuantity");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private List<ProductDisplay> getProductsByCategory(String categoryName) {
        List<ProductDisplay> products = new ArrayList<>();
        // Updated Query: Joins using category_idCategory
        String query = "SELECT p.idProduct, p.product_name, p.price, p.image_path, p.barcode " +
                "FROM product p JOIN category c ON p.category_idCategory = c.idCategory " +
                "WHERE c.category_name = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) products.add(new ProductDisplay(rs.getInt("idProduct"), rs.getString("product_name"), rs.getDouble("price"), rs.getString("image_path"), rs.getString("barcode")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return products;
    }

    private void displayProductsByCategory(String categoryName) {
        maindash.getChildren().clear();
        List<ProductDisplay> products = getProductsByCategory(categoryName);
        if (products.isEmpty()) {
            Label noContent = new Label("No products found.");
            maindash.getChildren().add(noContent);
            return;
        }
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(15);
        flowPane.setVgap(15);
        flowPane.setPadding(new Insets(15));
        maindash.getChildren().add(flowPane);
        AnchorPane.setTopAnchor(flowPane, 0.0);
        AnchorPane.setBottomAnchor(flowPane, 0.0);
        AnchorPane.setLeftAnchor(flowPane, 0.0);
        AnchorPane.setRightAnchor(flowPane, 0.0);
        for (ProductDisplay product : products) flowPane.getChildren().add(createProductCard(product));
    }

    private Node createProductCard(ProductDisplay product) {
        final double W = 200.0, H = 150.0;
        ImageView imageView = new ImageView();
        imageView.setFitWidth(W); imageView.setFitHeight(H);

        File imageFile = new File(IMAGE_STORAGE_DIR + File.separator + product.getImagePath());
        if (imageFile.exists()) {
            try { imageView.setImage(new Image(imageFile.toURI().toString(), W, H, false, true, true)); }
            catch (Exception e) { imageView.setImage(null); }
        }

        Label nameLabel = new Label(product.getName());
        nameLabel.setPrefWidth(W);
        nameLabel.setStyle("-fx-background-color: rgba(60,65,80,0.7); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5;");
        StackPane.setAlignment(nameLabel, Pos.TOP_LEFT);

        Label priceLabel = new Label(String.format("₱%.2f", product.getPrice()));
        priceLabel.setPrefWidth(W);
        priceLabel.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5;");
        StackPane.setAlignment(priceLabel, Pos.BOTTOM_LEFT);

        StackPane card = new StackPane();
        card.setPrefSize(W, H);
        card.setStyle("-fx-border-color: #000; -fx-cursor: hand; -fx-background-color: #f0f0f0;");
        card.getChildren().addAll(imageView.getImage() != null ? imageView : new Label(product.getName() + "\nNo Image"), nameLabel, priceLabel);

        card.setOnMouseClicked(e -> {
            if (currentOrderId == -1) showAlert(Alert.AlertType.ERROR, "Error", "Start a new order (F1) first.");
            else openQuantityDialog(String.valueOf(product.getId()), product.getName(), product.getPrice());
        });
        return card;
    }

    private void openQuantityDialog(String productId, String name, double price) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/quantity.fxml"));
            Parent root = loader.load();
            QuantityController controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            controller.setDialogStage(stage);
            controller.setProductInfo(productId, name, price);
            stage.showAndWait();
            if (controller.getEnteredQuantity() > 0) addItemToOrder(controller.getProductId(), name, price, controller.getEnteredQuantity());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void addItemToOrder(String productIdString, String name, double price, int quantity) {
        int productId;
        try { productId = Integer.parseInt(productIdString); } catch (NumberFormatException e) { return; }
        if (currentOrderId == -1) { showAlert(Alert.AlertType.ERROR, "Error", "Start a new order first."); return; }

        int stock = getProductStock(productId);
        Optional<OrderItem> existing = orderItems.stream().filter(i -> i.getProductId().equals(productIdString)).findFirst();
        int totalQty = quantity + (existing.map(OrderItem::getQuantity).orElse(0));

        if (totalQty > stock) {
            showAlert(Alert.AlertType.WARNING, "Out of Stock", "Stock limit: " + stock);
            return;
        }

        if (existing.isPresent()) {
            existing.get().setQuantity(totalQty);
            orderview.refresh();
            updateTotalPrice();
        } else {
            orderItems.add(new OrderItem(productIdString, name, price, quantity));
        }
    }

    @FXML private void handleDisplays(ActionEvent event) { displayProductsByCategory("Displays/Monitor"); }
    @FXML private void handleCom(ActionEvent event) { displayProductsByCategory("Computer Peripherals"); }
    @FXML private void handleAudio(ActionEvent event) { displayProductsByCategory("Audio Devices"); }
    @FXML private void handleCables(ActionEvent event) { displayProductsByCategory("Cables"); }
    @FXML public void handleNewOrder(ActionEvent event) { startNewOrderTransaction(true); }

    @FXML public void handlePayment(ActionEvent event) {
        if (currentOrderId == -1 || orderItems.isEmpty()) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/payment.fxml"));
            Parent root = loader.load();
            PaymentController controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            double total = orderItems.stream().mapToDouble(OrderItem::getTotal).sum();
            List<PaymentController.OrderItem> pItems = new ArrayList<>();
            for(OrderItem i : orderItems) pItems.add(new PaymentController.OrderItem(i.getProductId(), i.getName(), i.getQuantity(), i.getPrice(), i.getTotal()));

            controller.setOrderData(stage, currentOrderId, total, pItems);
            stage.showAndWait();

            PaymentController.PaymentResult res = controller.getPaymentResult();
            if (res != null && res.successful) {
                boolean itemsSaved = saveFullOrderTransaction(currentOrderId, orderItems);
                boolean invUpdated = updateInventory(orderItems);
                boolean paymentSaved = savePaymentLog(currentOrderId, res.amountPaid, res.change);

                if (itemsSaved && invUpdated && paymentSaved) {
                    List<ReceiptItem> rItems = orderItems.stream().map(i -> new ReceiptItem(i.getName(), i.getQuantity(), i.getPrice())).collect(Collectors.toList());
                    String content = ReceiptGenerator.generateReceiptContent(rItems, 0, 0, String.format("%04d", currentOrderId), res.amountPaid, res.change);

                    File dir = new File("receipts"); if(!dir.exists()) dir.mkdirs();
                    PdfGenerator.createReceiptPdf(content, "receipts/Order_" + currentOrderId + ".pdf");

                    receiptTextArea.setText(content);
                    receiptTransactionIdLabel.setText(String.format("%04d", currentOrderId));

                    orderItems.clear();
                    currentOrderId = -1;
                    updateOrderLabels();
                    setCategoryButtonsDisabled(true);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Critical Error", "Failed to save transaction data. Check Console.");
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void handleCancelOrder(ActionEvent event) {
        if (currentOrderId != -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cancel Order?", ButtonType.YES, ButtonType.NO);
            if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                deleteOrderFromDatabase(currentOrderId);
                orderItems.clear();
                currentOrderId = -1;
                updateOrderLabels();
                setCategoryButtonsDisabled(true);
            }
        }
    }

    private boolean deleteOrderFromDatabase(int orderId) {
        try (Connection conn = JDBC.getConnection()) {
            conn.createStatement().executeUpdate("DELETE FROM payment WHERE order_idOrder=" + orderId);
            conn.createStatement().executeUpdate("DELETE FROM orderdetails WHERE order_idOrder=" + orderId);
            conn.createStatement().executeUpdate("DELETE FROM `order` WHERE idOrder=" + orderId);
            return true;
        } catch (SQLException e) { return false; }
    }

    @FXML private void manageProduct(ActionEvent event) { try { openModalWindow("products.fxml", "Manage Products"); } catch(Exception e){} }
    @FXML private void manageTable(ActionEvent event) { try { openModalWindow("tables.fxml", "Manage Category"); } catch(Exception e){} }
    @FXML public void handleSalesReport(ActionEvent event) { try { openModalWindow("salesreport.fxml", "Sales Report"); } catch(Exception e){} }

    @FXML public void handleLogout(ActionEvent event) throws IOException {
        ((Stage)button_logout.getScene().getWindow()).setScene(new Scene(FXMLLoader.load(getClass().getResource("/controller/controller.fxml"))));
    }

    private void addGlobalKeyListener() {
        button_new_order.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().isFunctionKey()) {
                switch (event.getCode()) {
                    case F1: button_new_order.fire(); break;
                    case F2: button_payment.fire(); break;
                    case F3: button_cancel_order.fire(); break;
                    case F4: button_product_manage.fire(); break;
                    case F5: button_manage.fire(); break;
                    case F6: button_sales_report.fire(); break;
                    case F7: button_logout.fire(); break;
                }
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                String bc = barcodeBuffer.toString().trim();
                barcodeBuffer.setLength(0);
                if (!bc.isEmpty() && currentOrderId != -1) {
                    ProductDisplay p = getProductByBarcode(bc);
                    if (p != null) addItemToOrder(String.valueOf(p.getId()), p.getName(), p.getPrice(), 1);
                }
            } else {
                if (System.currentTimeMillis() - lastKeyTime > BARCODE_TIMEOUT) barcodeBuffer.setLength(0);
                lastKeyTime = System.currentTimeMillis();
                if (!event.getText().isEmpty()) barcodeBuffer.append(event.getText());
            }
        });
    }
}