package controller;

// This class represents a single item sold for the purpose of receipt generation.
public class ReceiptItem {
    private final String name;
    private final int quantity;
    private final double pricePerUnit;

    public ReceiptItem(String name, int quantity, double pricePerUnit) {
        this.name = name;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPricePerUnit() { return pricePerUnit; }
    public double getTotalPrice() { return quantity * pricePerUnit; }
}
