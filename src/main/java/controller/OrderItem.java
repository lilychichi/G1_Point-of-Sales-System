package controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for items displayed in the order TableView.
 * Uses JavaFX properties for observable data binding.
 */
public class OrderItem {

    // Properties corresponding to TableView columns and internal product ID
    private final StringProperty productId;
    private final StringProperty name;
    private final DoubleProperty price;
    private final IntegerProperty quantity;
    private final DoubleProperty total; //price * quantity

    /**
     * Constructor
     * @param productId The unique database ID of the product (stored as String for consistency, though often int in DB)
     * @param name The name of the product
     * @param price The unit price of the product
     * @param quantity The initial quantity ordered
     */
    public OrderItem(String productId, String name, double price, int quantity) {
        this.productId = new SimpleStringProperty(productId); // Correctly accepts String
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.total = new SimpleDoubleProperty(price * quantity);

        // Listener to automatically update the 'total' when 'quantity' changes
        this.quantity.addListener((obs, oldVal, newVal) ->
                updateTotal(this.price.get(), newVal.intValue())
        );
        // Listener to automatically update the 'total' when 'price' changes
        this.price.addListener((obs, oldVal, newVal) ->
                updateTotal(newVal.doubleValue(), this.quantity.get())
        );
    }

    /**
     * Internal method to recalculate and set the total property.
     */
    private void updateTotal(double newPrice, int newQuantity) {
        this.total.set(newPrice * newQuantity);
    }

    public String getProductId() {
        return productId.get();
    }

    public String getName() {
        return name.get();
    }

    public double getPrice() {
        return price.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public double getTotal() {
        return total.get();
    }

    public StringProperty productIdProperty() {
        return productId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    /**
     * Setter for quantity. Automatically updates the total property via the listener.
     * @param quantity The new quantity.
     */
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    // Setter for price (if needed)
    public void setPrice(double price) {
        this.price.set(price);
    }
}
