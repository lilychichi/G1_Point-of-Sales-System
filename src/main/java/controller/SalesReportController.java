package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class SalesReportController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private TableView<SalesReportData> salesTable;
    @FXML private TableColumn<SalesReportData, String> productNameColumn;
    @FXML private TableColumn<SalesReportData, Integer> quantityColumn;
    @FXML private TableColumn<SalesReportData, Double> subtotalColumn;
    @FXML private Label totalSalesLabel;

    private final ObservableList<SalesReportData> salesData = FXCollections.observableArrayList();
    private static final DateTimeFormatter REPORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalQuantitySold"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalSubtotal"));

        salesTable.setItems(salesData);
        datePicker.setValue(LocalDate.now());
        datePicker.setOnAction(event -> fetchSalesReport());

        fetchSalesReport();
    }

    @FXML
    private void fetchSalesReport() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            salesData.clear();
            totalSalesLabel.setText("₱0.00");
            return;
        }

        salesData.clear();
        double grandTotal = 0.0;

        // DIRECT FK QUERY matching your LoggedInController logic
        String sql = "SELECT " +
                "    P.product_name AS productName, " +
                "    SUM(OD.quantity) AS TotalQuantitySold, " +
                "    SUM(OD.subtotal) AS TotalSubtotal " +
                "FROM " +
                "    orderDetails OD " +
                "JOIN " +
                "    product P ON OD.product_idProduct = P.idProduct " +
                "JOIN " +
                "    `order` O ON OD.order_idOrder = O.idOrder " +
                "WHERE " +
                "    O.orderDate = ? " +
                "GROUP BY " +
                "    P.product_name " +
                "ORDER BY " +
                "    TotalSubtotal DESC";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String dateString = selectedDate.format(REPORT_DATE_FORMATTER);
            pstmt.setString(1, dateString);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No sales data found for date: " + dateString);
                }
                while (rs.next()) {
                    String productName = rs.getString("productName");
                    int quantity = rs.getInt("TotalQuantitySold");
                    double subtotal = rs.getDouble("TotalSubtotal");

                    salesData.add(new SalesReportData(productName, quantity, subtotal));
                    grandTotal += subtotal;
                }
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }

        totalSalesLabel.setText(String.format("₱%.2f", grandTotal));
    }
}