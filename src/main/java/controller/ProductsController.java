package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ProductsController implements Initializable {

    @FXML private TextField tf_id;
    @FXML private TextField tf_name;
    @FXML private TextField tf_description;
    @FXML private TextField tf_price;
    @FXML private ComboBox<String> tf_cat;
    @FXML private TextField stock_quantity;
    @FXML private TextField barcode;
    @FXML private ImageView imageBox;
    @FXML private Button button_save;
    @FXML private Button button_update;
    @FXML private Button button_delete;
    @FXML private Button button_add;
    @FXML private Button button_browse;

    @FXML private TableView<Product> button_manage;
    @FXML private TableColumn<Product, Integer> id;
    @FXML private TableColumn<Product, String> name;
    @FXML private TableColumn<Product, String> desc;
    @FXML private TableColumn<Product, Double> price;
    @FXML private TableColumn<Product, String> cat;
    @FXML private TableColumn<Product, String> stat;
    @FXML private TableColumn<Product, String> bcode;
    @FXML private TableColumn<Product, Integer> quantity;

    private Map<String, Integer> categoryMap = new HashMap<>();
    private File selectedFile;
    private static final String IMAGE_STORAGE_DIR = "product_images";

    public static class Product {
        private final int id;
        private final String name;
        private final String description;
        private final double price;
        private final String categoryName;
        private final int stockQuantity;
        private final String statusDisplay;
        private final String barcodeValue;
        private final String imagePath;

        public Product(int id, String name, String description, double price, String categoryName, int stockQuantity, String barcodeValue, String imagePath) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.categoryName = categoryName;
            this.stockQuantity = stockQuantity;
            this.barcodeValue = barcodeValue;
            this.imagePath = imagePath;

            if (stockQuantity > 10) {
                this.statusDisplay = "Available";
            } else if (stockQuantity > 0) {
                this.statusDisplay = "Low Stock";
            } else {
                this.statusDisplay = "Out of Stock";
            }
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDesc() { return description; }
        public double getPrice() { return price; }
        public String getCat() { return categoryName; }
        public String getStat() { return statusDisplay; }
        public int getStockQuantity() { return stockQuantity; }
        public String getBcode() { return barcodeValue; }
        public String getImagePath() { return imagePath; }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Path path = Paths.get(IMAGE_STORAGE_DIR);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                System.err.println("Error creating image storage directory: " + e.getMessage());
            }
        }

        loadCategories();
        showProducts();
        button_manage.setOnMouseClicked(this::handleMouseAction);
    }

    private void loadCategories() {
        categoryMap.clear();
        String query = "SELECT idCategory, category_name FROM `category`";
        ObservableList<String> categoryNames = FXCollections.observableArrayList();

        try (Connection conn = JDBC.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                String name = rs.getString("category_name");
                int id = rs.getInt("idCategory");
                categoryNames.add(name);
                categoryMap.put(name, id);
            }

            tf_cat.setItems(categoryNames);

            if (!categoryNames.isEmpty()) {
                tf_cat.getSelectionModel().selectFirst();
            } else {
                tf_cat.getSelectionModel().clearSelection();
            }

        } catch (SQLException ex) {
            System.err.println("Database error loading categories: " + ex.getMessage());
        }
    }

    public void showProducts() {
        ObservableList<Product> productList = getProductList();

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        desc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        cat.setCellValueFactory(new PropertyValueFactory<>("cat"));
        stat.setCellValueFactory(new PropertyValueFactory<>("stat"));
        bcode.setCellValueFactory(new PropertyValueFactory<>("bcode"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        button_manage.setItems(productList);
    }

    private ObservableList<Product> getProductList() {
        ObservableList<Product> list = FXCollections.observableArrayList();

        // Updated Query to use Direct FK
        String query = "SELECT p.idProduct, p.product_name, p.description, p.price, c.category_name, p.StockQuantity, p.barcode, p.image_path " +
                "FROM `product` p " +
                "JOIN `category` c ON p.category_idCategory = c.idCategory"; // Using Direct FK

        try (Connection conn = JDBC.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("idProduct"),
                        rs.getString("product_name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category_name"),
                        rs.getInt("StockQuantity"),
                        rs.getString("barcode"),
                        rs.getString("image_path")
                );
                list.add(product);
            }
        } catch (SQLException ex) {
            System.err.println("Database error retrieving products: " + ex.getMessage());
        }
        return list;
    }

    @FXML
    private void handleMouseAction(MouseEvent event) {
        Product product = button_manage.getSelectionModel().getSelectedItem();
        if (product != null) {
            tf_id.setText(String.valueOf(product.getId()));
            tf_name.setText(product.getName());
            tf_description.setText(product.getDesc());
            tf_price.setText(String.valueOf(product.getPrice()));
            tf_cat.getSelectionModel().select(product.getCat());
            stock_quantity.setText(String.valueOf(product.getStockQuantity()));
            barcode.setText(product.getBcode());

            String imagePath = product.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(IMAGE_STORAGE_DIR + File.separator + imagePath);
                if (imageFile.exists()) {
                    try {
                        Image image = new Image(imageFile.toURI().toString());
                        imageBox.setImage(image);
                        this.selectedFile = imageFile;
                    } catch (Exception e) {
                        imageBox.setImage(null);
                        this.selectedFile = null;
                    }
                } else {
                    imageBox.setImage(null);
                    this.selectedFile = null;
                }
            } else {
                imageBox.setImage(null);
                this.selectedFile = null;
            }
        }
    }

    @FXML private void saveTable(ActionEvent event) { insertRecord(); }
    @FXML private void updateTable(ActionEvent event) { updateRecord(); }
    @FXML private void deleteTable(ActionEvent event) { deleteRecord(); }

    @FXML
    private void browseimg(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                imageBox.setImage(image);
                this.selectedFile = file;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML private void add_cat(ActionEvent event) {
        try {
            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            openModalWindow("/controller/tables.fxml", "Manage Category", ownerStage);
            loadCategories();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openModalWindow(String fxmlPath, String title, Stage ownerStage) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) throw new IOException("FXML file not found: " + fxmlPath);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerStage);
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private void insertRecord() {
        String name = tf_name.getText();
        String desc = tf_description.getText();
        String priceText = tf_price.getText();
        String categoryName = tf_cat.getValue();
        String quantityText = stock_quantity.getText();
        String barcodeValue = barcode.getText();
        Stage currentStage = (Stage) tf_name.getScene().getWindow();

        if (selectedFile == null) {
            showAlert(currentStage, "Image Required", "Please browse and select an image.");
            return;
        }
        if (name.isEmpty() || priceText.isEmpty() || categoryName == null || quantityText.isEmpty() || barcodeValue.isEmpty()) {
            showAlert(currentStage, "Missing Fields", "Error: All text fields are required.");
            return;
        }

        try {
            double priceValue = Double.parseDouble(priceText);
            // FIX: Ensure we get a valid ID or default to 1
            Integer categoryIdObj = categoryMap.get(categoryName);
            int categoryId = (categoryIdObj != null) ? categoryIdObj : 1;

            int quantityValue = Integer.parseInt(quantityText);

            String storedImagePath = saveImageFile(selectedFile);
            if (storedImagePath == null) {
                showAlert(currentStage,"Image Save Error", "Failed to save the image file.");
                return;
            }

            // FIX: Insert directly into product with category_idCategory (No Junction Table)
            String productInsertQuery = "INSERT INTO `product` (product_name, description, price, StockQuantity, barcode, image_path, category_idCategory) VALUES (?, ?, ?, ?, ?, ?, ?)";

            executeInsert(productInsertQuery, name, desc, priceValue, quantityValue, barcodeValue, storedImagePath, categoryId);

            showProducts();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(currentStage,"Invalid Input", "Price and Quantity must be numbers.");
        }
    }

    private void updateRecord() {
        Product selectedProduct = button_manage.getSelectionModel().getSelectedItem();
        Stage currentStage = (Stage) tf_name.getScene().getWindow();

        if (selectedProduct == null) {
            showAlert(currentStage,"Missing Selection", "Select a product first.");
            return;
        }

        String name = tf_name.getText();
        String desc = tf_description.getText();
        String priceText = tf_price.getText();
        String categoryName = tf_cat.getValue();
        String quantityText = stock_quantity.getText();
        String barcodeValue = barcode.getText();

        try {
            int id = selectedProduct.getId();
            double priceValue = Double.parseDouble(priceText);
            // FIX: Get Category ID
            Integer categoryIdObj = categoryMap.get(categoryName);
            int categoryId = (categoryIdObj != null) ? categoryIdObj : 1;

            int quantityValue = Integer.parseInt(quantityText);

            String storedImagePath = selectedProduct.getImagePath();
            if (selectedFile != null && (storedImagePath == null || !selectedFile.getAbsolutePath().endsWith(storedImagePath))) {
                storedImagePath = saveImageFile(selectedFile);
            }

            // FIX: Update product table directly including category_idCategory
            String productUpdateQuery = "UPDATE `product` SET product_name = ?, description = ?, price = ?, StockQuantity = ?, barcode = ?, image_path = ?, category_idCategory = ? WHERE idProduct = ?";

            executeUpdate(productUpdateQuery, name, desc, priceValue, quantityValue, barcodeValue, storedImagePath, categoryId, id);

            showProducts();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(currentStage,"Invalid Input", "Price and Quantity must be numbers.");
        }
    }

    private void deleteRecord() {
        Product selectedProduct = button_manage.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) return;

        int id = selectedProduct.getId();

        // FIX: Only need to delete from product table now
        String deleteProductQuery = "DELETE FROM `product` WHERE idProduct = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteProductQuery)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        showProducts();
        clearFields();
    }

    private void clearFields() {
        tf_id.setText("");
        tf_name.setText("");
        tf_description.setText("");
        tf_price.setText("");
        tf_cat.getSelectionModel().clearSelection();
        stock_quantity.setText("");
        barcode.setText("");
        imageBox.setImage(null);
        this.selectedFile = null;
    }

    private void showAlert(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (owner != null) alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.showAndWait();
    }

    private String saveImageFile(File sourceFile) {
        if (sourceFile == null) return null;
        try {
            String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
            Path destination = Paths.get(IMAGE_STORAGE_DIR, fileName);
            Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper for INSERT
    private void executeInsert(String query, String name, String desc, double price, int quantity, String barcodeValue, String imagePath, int categoryId) {
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setDouble(3, price);
            ps.setInt(4, quantity);
            ps.setString(5, barcodeValue);
            ps.setString(6, imagePath);
            ps.setInt(7, categoryId); // Category ID is the 7th param
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Insert Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper for UPDATE
    private void executeUpdate(String query, String name, String desc, double price, int quantity, String barcodeValue, String imagePath, int categoryId, int id) {
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setDouble(3, price);
            ps.setInt(4, quantity);
            ps.setString(5, barcodeValue);
            ps.setString(6, imagePath);
            ps.setInt(7, categoryId);
            ps.setInt(8, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Update Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}