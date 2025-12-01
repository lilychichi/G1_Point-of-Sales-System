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
import javafx.scene.image.Image;        // For Image object
import javafx.scene.image.ImageView;     // For ImageView
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;       // For file browsing dialog
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;              // For getting the current stage
import java.io.File;                   // For handling the selected file
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;            // For copying files
import java.nio.file.Path;             // For file paths
import java.nio.file.Paths;            // For file paths
import java.nio.file.StandardCopyOption; // For file copy options
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ProductsController implements Initializable {

    // FXML Fields
    @FXML private TextField tf_id;
    @FXML private TextField tf_name;
    @FXML private TextField tf_description;
    @FXML private TextField tf_price;
    @FXML private ComboBox<String> tf_cat;
    @FXML private TextField stock_quantity;
    @FXML private TextField barcode;        // fx:id="barcode"
    @FXML private ImageView imageBox;       // fx:id="imageBox"
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
    @FXML private TableColumn<Product, String> bcode; // fx:id="bcode"
    @FXML private TableColumn<Product, Integer> quantity;

    private Map<String, Integer> categoryMap = new HashMap<>();
    private File selectedFile; // Stores the currently selected image file
    // Define a directory to save product images
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
        private final String imagePath; // Image path field

        public Product(int id, String name, String description, double price, String categoryName, int stockQuantity, String barcodeValue, String imagePath) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.categoryName = categoryName;
            this.stockQuantity = stockQuantity;
            this.barcodeValue = barcodeValue;
            this.imagePath = imagePath; // Initialize imagePath

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
        public int getStockQuantity() { return stockQuantity; } // Crucial getter for the TableView binding
        public String getBcode() { return barcodeValue; }
        public String getImagePath() { return imagePath; } // Getter for image path
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Ensure the image storage directory exists
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

        // Select the image_path column (p.image_path)
        String query = "SELECT p.idProduct, p.product_name, p.description, p.price, c.category_name, p.StockQuantity, p.barcode, p.image_path " +
                "FROM `product` p " +
                "JOIN `product_has_category` phc ON p.idProduct = phc.Product_idProduct " +
                "JOIN `category` c ON phc.Category_idCategory = c.idCategory";

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
                        rs.getString("image_path") // Pass image_path value to constructor
                );
                list.add(product);
            }
        } catch (SQLException ex) {
            System.err.println("Database error retrieving products. Check if 'barcode' and 'image_path' columns exist: " + ex.getMessage());
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

            // Logic to load image from stored path
            String imagePath = product.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(IMAGE_STORAGE_DIR + File.separator + imagePath);
                if (imageFile.exists()) {
                    try {
                        Image image = new Image(imageFile.toURI().toString());
                        imageBox.setImage(image);
                        this.selectedFile = imageFile; // Set selectedFile to currently displayed image
                    } catch (Exception e) {
                        System.err.println("Error loading image from path: " + imagePath + " - " + e.getMessage());
                        imageBox.setImage(null); // Clear image if loading fails
                        this.selectedFile = null;
                    }
                } else {
                    System.err.println("Image file not found at path: " + imageFile.getAbsolutePath());
                    imageBox.setImage(null); // Clear image if file doesn't exist
                    this.selectedFile = null;
                }
            } else {
                imageBox.setImage(null); // Clear image if no path is stored
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

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                imageBox.setImage(image);
                this.selectedFile = file; // Store the selected file for later saving

            } catch (Exception e) {
                System.err.println("Error loading image for preview: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML private void add_cat(ActionEvent event) {
        try {
            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            openModalWindow("/controller/tables.fxml", "Manage Category", ownerStage);
            loadCategories(); // Reload categories after modal closes
        } catch (IOException e) {
            System.err.println("Error opening Tables window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openModalWindow(String fxmlPath, String title, Stage ownerStage) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);

        if (fxmlUrl == null) {
            throw new IOException("FXML file not found! Please check the file path: " + fxmlPath +
                    ". Ensure the file is correctly placed in your resources folder.");
        }

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

        // Get the current window (Stage) from any field on the scene
        Stage currentStage = (Stage) tf_name.getScene().getWindow();

        // Require an image to be selected
        if (selectedFile == null) {
            showAlert(currentStage, "Image Required", "Please browse and select an image for the product.");
            return;
        }

        if (name.isEmpty() || priceText.isEmpty() || categoryName == null || quantityText.isEmpty() || barcodeValue.isEmpty()) {
            showAlert(currentStage, "Missing Fields", "Error: All text fields (including barcode and category) are required.");
            return;
        }

        try {
            double priceValue = Double.parseDouble(priceText);
            int categoryId = categoryMap.get(categoryName);
            int quantityValue = Integer.parseInt(quantityText);

            // Save the image file and get its stored path
            String storedImagePath = saveImageFile(selectedFile);
            if (storedImagePath == null) {
                showAlert(currentStage,"Image Save Error", "Failed to save the image file.");
                return;
            }

            // Include 'barcode' and 'image_path'
            String productInsertQuery = "INSERT INTO `product` (product_name, description, price, StockQuantity, barcode, image_path) VALUES (?, ?, ?, ?, ?, ?)";

            // Pass storedImagePath to executeInsertAndGetID
            int newProductId = executeInsertAndGetID(productInsertQuery, name, desc, priceValue, quantityValue, barcodeValue, storedImagePath);
            if (newProductId != -1) {
                String junctionInsertQuery = "INSERT INTO `product_has_category` (Product_idProduct, Category_idCategory) VALUES (?, ?)";
                executeQuery(junctionInsertQuery, newProductId, categoryId);
            }

            showProducts();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(currentStage,"Invalid Input", "Error: Price and Quantity must be valid numbers.");
        } catch (NullPointerException e) {
            showAlert(currentStage,"Category Error", "Error: Selected category is invalid. Check database contents.");
        }
    }

    private void updateRecord() {
        Product selectedProduct = button_manage.getSelectionModel().getSelectedItem();
        String name = tf_name.getText();
        String desc = tf_description.getText();
        String priceText = tf_price.getText();
        String categoryName = tf_cat.getValue();
        String quantityText = stock_quantity.getText();
        String barcodeValue = barcode.getText();

        // Require an image to be selected (or an existing one to be loaded)
        Stage currentStage = (Stage) tf_name.getScene().getWindow();
        if (selectedFile == null) {
            showAlert(currentStage,"Image Required", "Please browse and select an image for the product.");
            return;
        }

        if (selectedProduct == null || name.isEmpty() || priceText.isEmpty() || categoryName == null || quantityText.isEmpty() || barcodeValue.isEmpty()) {
            showAlert(currentStage,"Missing Fields", "Error: Select a product from the table and complete all text fields (including barcode and category).");
            return;
        }

        try {
            int id = selectedProduct.getId();
            double priceValue = Double.parseDouble(priceText);
            int categoryId = categoryMap.get(categoryName);
            int quantityValue = Integer.parseInt(quantityText);

            // Handle image update - only save if selectedFile is different from original path
            String storedImagePath = selectedProduct.getImagePath(); // Default to existing path
            if (selectedFile != null && (storedImagePath == null || !selectedFile.getAbsolutePath().endsWith(storedImagePath))) {
                // Delete old image if it exists and is different
                if (storedImagePath != null && !storedImagePath.isEmpty()) {
                    File oldImage = new File(IMAGE_STORAGE_DIR + File.separator + storedImagePath);
                    if (oldImage.exists() && !oldImage.getAbsolutePath().equals(selectedFile.getAbsolutePath())) {
                        Files.deleteIfExists(oldImage.toPath());
                    }
                }
                storedImagePath = saveImageFile(selectedFile); // Save new image
                if (storedImagePath == null) {
                    showAlert(currentStage,"Image Save Error", "Failed to save the new image file.");
                    return;
                }
            }

            String productUpdateQuery = "UPDATE `product` SET product_name = ?, description = ?, price = ?, StockQuantity = ?, barcode = ?, image_path = ? WHERE idProduct = ?";

            executeQuery(productUpdateQuery, name, desc, priceValue, quantityValue, barcodeValue, storedImagePath, id);

            String deleteOldLinkQuery = "DELETE FROM `product_has_category` WHERE Product_idProduct = ?";
            executeQuery(deleteOldLinkQuery, id);

            String insertNewLinkQuery = "INSERT INTO `product_has_category` (Product_idProduct, Category_idCategory) VALUES (?, ?)";
            executeQuery(insertNewLinkQuery, id, categoryId);

            showProducts();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(currentStage,"Invalid Input", "Error: Price and Quantity must be valid numbers.");
        } catch (NullPointerException e) {
            showAlert(currentStage,"Category Error", "Error: Selected category is invalid. Check database contents.");
        } catch (IOException e) { // Catch IOException for image deletion
            showAlert(currentStage,"File Error", "Error deleting old image: " + e.getMessage());
        }
    }

    private void deleteRecord() {
        Product selectedProduct = button_manage.getSelectionModel().getSelectedItem();
        Stage currentStage = (Stage) tf_name.getScene().getWindow();
        if (selectedProduct == null) {
            showAlert(currentStage,"No Selection", "Error: Select a record to delete.");
            return;
        }

        int id = selectedProduct.getId();
        String imagePathToDelete = selectedProduct.getImagePath(); // Get image path

        String deleteJunctionQuery = "DELETE FROM `product_has_category` WHERE Product_idProduct = ?";
        executeQuery(deleteJunctionQuery, id);

        String deleteProductQuery = "DELETE FROM `product` WHERE idProduct = ?";
        executeQuery(deleteProductQuery, id);

        // Delete the associated image file from disk
        if (imagePathToDelete != null && !imagePathToDelete.isEmpty()) {
            File imageFile = new File(IMAGE_STORAGE_DIR + File.separator + imagePathToDelete);
            try {
                if (imageFile.exists()) {
                    Files.deleteIfExists(imageFile.toPath());
                    System.out.println("Deleted image file: " + imageFile.getName());
                }
            } catch (IOException e) {
                System.err.println("Error deleting image file: " + imageFile.getName() + " - " + e.getMessage());
                // Don't prevent deletion of DB record if file deletion fails
            }
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
        this.selectedFile = null; // Clear the selected file reference
    }

    // To show alert messages
    private void showAlert(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (owner != null) {
            alert.initOwner(owner);
        }

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Set modality to ensure it blocks input to the owner
        alert.initModality(Modality.WINDOW_MODAL);

        alert.showAndWait();
    }

    // Saves the image file to the designated directory
    private String saveImageFile(File sourceFile) {
        if (sourceFile == null) return null;

        try {
            String fileName = System.currentTimeMillis() + "_" + sourceFile.getName(); // Unique filename
            Path destination = Paths.get(IMAGE_STORAGE_DIR, fileName);
            Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return fileName; // Return just the filename, not the full path, for DB storage
        } catch (IOException e) {
            System.err.println("Error saving image file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private int executeInsertAndGetID(String query, String name, String desc, double price, int quantity, String barcodeValue, String imagePath) {
        System.out.println("Executing Insert: " + query);
        int generatedKey = -1;

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setDouble(3, price);
            ps.setInt(4, quantity);
            ps.setString(5, barcodeValue);
            ps.setString(6, imagePath); // Set image_path parameter

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("SQL Execution Error for insert: " + query);
            e.printStackTrace();
        }
        return generatedKey;
    }

    private void executeQuery(String query, int param1, int param2) {
        System.out.println("Executing Query: " + query + " with params: " + param1 + ", " + param2);

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, param1);
            ps.setInt(2, param2);
            ps.executeUpdate();
            System.out.println("Query executed successfully.");

        } catch (SQLException e) {
            System.err.println("SQL Execution Error for query: " + query);
            e.printStackTrace();
        }
    }

    private void executeQuery(String query, int id) {
        System.out.println("Executing Query: " + query + " with id: " + id);

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Query executed successfully.");

        } catch (SQLException e) {
            System.err.println("SQL Execution Error for query: " + query);
            e.printStackTrace();
        }
    }

    // executeQuery now accepts 'barcodeValue' and 'imagePath' for update
    private void executeQuery(String query, String name, String desc, double price, int quantity, String barcodeValue, String imagePath, int id) {
        System.out.println("Executing Query: " + query + " for id: " + id);

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setDouble(3, price);
            ps.setInt(4, quantity);
            ps.setString(5, barcodeValue);
            ps.setString(6, imagePath); // Set image_path parameter for update
            ps.setInt(7, id);

            ps.executeUpdate();
            System.out.println("Query executed successfully.");

        } catch (SQLException e) {
            System.err.println("SQL Execution Error for query: " + query);
            e.printStackTrace();
        }
    }
}