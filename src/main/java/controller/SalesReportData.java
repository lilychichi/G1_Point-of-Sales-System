package controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

// This model holds aggregated sales data for the report
public class SalesReportData {
    private final SimpleStringProperty productName;
    private final SimpleIntegerProperty totalQuantitySold;
    private final SimpleDoubleProperty totalSubtotal;

    public SalesReportData(String productName, int totalQuantitySold, double totalSubtotal) {
        this.productName = new SimpleStringProperty(productName);
        this.totalQuantitySold = new SimpleIntegerProperty(totalQuantitySold);
        this.totalSubtotal = new SimpleDoubleProperty(totalSubtotal);
    }

    // Getters for TableView
    public String getProductName() {
        return productName.get();
    }

    public int getTotalQuantitySold() {
        return totalQuantitySold.get();
    }

    public double getTotalSubtotal() {
        return totalSubtotal.get();
    }
}
