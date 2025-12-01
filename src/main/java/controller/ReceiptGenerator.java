package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceiptGenerator {

    // Constants
    private static final String SHOP_NAME = "QTen";
    private static final String ADDRESS = "San Mateo, Rizal";
    private static final int LINE_WIDTH = 40;
    private static final String CURRENCY = "₱";

    /**
     * Updated to accept amountPaid and change.
     */
    public static String generateReceiptContent(List<ReceiptItem> orderItems, double discountRate, double taxRate, String orderId, double amountPaid, double change) {
        LocalDateTime now = LocalDateTime.now();
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        StringBuilder receipt = new StringBuilder();

        // --- 1. Header ---
        receipt.append(centerText(SHOP_NAME, LINE_WIDTH)).append("\n");
        receipt.append(centerText(ADDRESS, LINE_WIDTH)).append("\n");
        receipt.append(new String(new char[LINE_WIDTH]).replace('\0', '-')).append("\n");

        // --- 2. Transaction Details ---
        String date = now.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        receipt.append(String.format("Date: %-20s Time: %s\n", date, time));
        receipt.append(String.format("Order ID: %s\n", orderId));
        receipt.append(new String(new char[LINE_WIDTH]).replace('\0', '-')).append("\n");

        // --- 3. Itemized List ---
        receipt.append(String.format("%-3s %-19s %7s %7s\n", "Qty", "Item Name", "Price", "Total"));
        receipt.append(new String(new char[LINE_WIDTH]).replace('\0', '-')).append("\n");

        double subtotal = 0;

        for (ReceiptItem item : orderItems) {
            String name = item.getName().length() > 19 ? item.getName().substring(0, 16) + "..." : item.getName();
            receipt.append(String.format("%-3d %-19s %7s %7s\n",
                    item.getQuantity(),
                    name,
                    CURRENCY + df.format(item.getPricePerUnit()),
                    CURRENCY + df.format(item.getTotalPrice())
            ));
            subtotal += item.getTotalPrice();
        }
        receipt.append(new String(new char[LINE_WIDTH]).replace('\0', '-')).append("\n");

        // --- 4. Financial Summary ---
        double discountAmount = subtotal * discountRate;
        double taxableBase = subtotal - discountAmount;
        double taxAmount = taxableBase * taxRate;
        double finalTotal = taxableBase + taxAmount;

        // Subtotal
        receipt.append(String.format("%-25s %14s\n", "Subtotal:", CURRENCY + df.format(subtotal)));

        // Discount
        receipt.append(String.format("%-25s %14s\n",
                "Discount (" + df.format(discountRate * 100) + "%):",
                "-" + CURRENCY + df.format(discountAmount)
        ));

        // VAT/Tax
        receipt.append(String.format("%-25s %14s\n",
                "VAT/Tax (" + df.format(taxRate * 100) + "%):",
                CURRENCY + df.format(taxAmount)
        ));

        receipt.append(new String(new char[LINE_WIDTH]).replace('\0', '=')).append("\n");

        // Total Due
        receipt.append(String.format("%-25s %14s\n", "TOTAL DUE:", CURRENCY + df.format(finalTotal)));

        // --- Payment Details ---
        receipt.append(String.format("%-25s %14s\n", "Amount Paid:", CURRENCY + df.format(amountPaid)));
        receipt.append(String.format("%-25s %14s\n", "Change:", CURRENCY + df.format(change)));

        receipt.append(new String(new char[LINE_WIDTH]).replace('\0', '=')).append("\n\n");

        // --- 5. Footer ---
        receipt.append(centerText("THANK YOU FOR SHOPPING AT QTen", LINE_WIDTH)).append("\n");
        receipt.append(centerText("Hope to see you again soon!", LINE_WIDTH)).append("\n");

        return receipt.toString();
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int padding = width - text.length();
        int left = padding / 2;
        return String.format("%" + (left + text.length()) + "s%" + (padding - left) + "s", text, "");
    }
}