package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement; // Imported PreparedStatement
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.sql.SQLException;

public class TablesController implements Initializable {

    @FXML
    private TextField tf_tablename;

    @FXML
    private Button button_save;

    @FXML
    private Button button_update;

    @FXML
    private Button button_delete;

    @FXML
    private TableView<Tables> tableTables;

    @FXML
    private TableColumn<Tables, Integer> col_id;

    @FXML
    private TableColumn<Tables, String> col_name;

    /**
     * Inner class for the TableView to hold category data.
     * Maps to idCategory and category_name.
     */
    public static class Tables {
        private final int id;
        private final String name;

        public Tables(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showTable();
        tableTables.setOnMouseClicked(this::handleMouseAction);
    }

    @FXML
    private void handleMouseAction(MouseEvent event) {
        Tables table = tableTables.getSelectionModel().getSelectedItem();
        if (table != null) {
            tf_tablename.setText(table.getName());
        }
    }


    @FXML
    private void saveTable(ActionEvent event){
        insertRecord();
    }

    @FXML
    private void updateTable(ActionEvent event){
        updateRecord();
    }

    @FXML
    private void deleteTable(ActionEvent event){
        deleteRecord();
    }


    // DATA DISPLAY
    public void showTable() {
        ObservableList<Tables> list = getTableList();

        // Note: PropertyValueFactory looks for methods like getId() and getName() in the Tables class
        col_id.setCellValueFactory (new PropertyValueFactory<>("id"));
        col_name.setCellValueFactory (new PropertyValueFactory<>("name"));
        tableTables.setItems(list);
    }

    // --- CRUD: CORE OPERATIONS ---

    // INSERT (Save)
    private void insertRecord(){
        String name = tf_tablename.getText();

        if(name.isEmpty()){
            System.err.println("Error: Table name cannot be empty.");
            return;
        }

        // Use PreparedStatement placeholder (?)
        String query = "INSERT INTO `category` (category_name) VALUES (?)";

        // Execute using the safe method
        executeModificationQuery(query, name);
        showTable();
        tf_tablename.setText("");
    }

    // UPDATE
    private void updateRecord(){
        Tables selectedTable = tableTables.getSelectionModel().getSelectedItem();
        String newName = tf_tablename.getText();

        if (selectedTable == null || newName.isEmpty()) {
            System.err.println("Error: Select a record and enter a new name.");
            return;
        }

        int id = selectedTable.getId();

        // Use PreparedStatement placeholders (?, ?)
        String query = "UPDATE `category` SET category_name = ? WHERE idCategory = ?";

        // Execute using the safe method
        executeModificationQuery(query, newName, id);
        showTable();
        tf_tablename.setText("");
    }

    // DELETE
    private void deleteRecord(){
        Tables selectedTable = tableTables.getSelectionModel().getSelectedItem();

        if (selectedTable == null) {
            System.err.println("Error: Select a record to delete.");
            return;
        }

        int id = selectedTable.getId();

        // Use PreparedStatement placeholder (?)
        String query = "DELETE FROM `category` WHERE idCategory = ?";

        // Execute using the safe method
        executeModificationQuery(query, id);
        showTable();
        tf_tablename.setText("");
    }

    // CRUD: EXECUTE SAFE QUERIES

    // For DELETE operations (1 integer parameter)
    private void executeModificationQuery(String query, int id) {
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

    // For INSERT operations (1 string parameter)
    private void executeModificationQuery(String query, String name) {
        System.out.println("Executing Query: " + query + " with name: " + name);

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, name);
            ps.executeUpdate();
            System.out.println("Query executed successfully.");

        } catch (SQLException e) {
            System.err.println("SQL Execution Error for query: " + query);
            e.printStackTrace();
        }
    }

    // For UPDATE operations (1 string and 1 integer parameter)
    private void executeModificationQuery(String query, String name, int id) {
        System.out.println("Executing Query: " + query + " for id: " + id);

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("Query executed successfully.");

        } catch (SQLException e) {
            System.err.println("SQL Execution Error for query: " + query);
            e.printStackTrace();
        }
    }


    //  CRUD: READ LOGIC (Still safe with Statement as no user input is used)
    private ObservableList<Tables> getTableList()  {
        ObservableList<Tables> tableList = FXCollections.observableArrayList();


        String query = "SELECT idCategory, category_name FROM `category`";

        try (Connection conn = JDBC.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while(rs.next()){

                Tables tables = new Tables(rs.getInt("idCategory"), rs.getString("category_name"));
                tableList.add(tables);
            }
        } catch (SQLException ex) {
            System.err.println("Database error during table list retrieval: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("An unexpected error occurred: " + ex.getMessage());
        }

        return tableList;
    }
}