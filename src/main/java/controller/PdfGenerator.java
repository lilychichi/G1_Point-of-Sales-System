package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator {

    // Define a simple font for the receipt text
    private static final Font RECEIPT_FONT = new Font(Font.FontFamily.COURIER, 10, Font.NORMAL);

    /**
     * Converts a string of receipt content into a PDF file.
     * @param receiptContent The text content of the receipt.
     * @param fileName The desired name for the output PDF file.
     * @return true if the PDF was created successfully, false otherwise.
     */
    public static boolean createReceiptPdf(String receiptContent, String fileName) {
        Rectangle receiptSize = new Rectangle(250f, 10000f); // Width=226pts, Height=10000pts (effectively continuous)

        Document document = new Document(receiptSize);
        document.setMargins(5, 5, 5, 5);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            String[] lines = receiptContent.split("\\n");
            for (String line : lines) {
                Paragraph paragraph = new Paragraph(line, RECEIPT_FONT);
                paragraph.setAlignment(Element.ALIGN_LEFT);
                paragraph.setSpacingAfter(0);
                paragraph.setSpacingBefore(0);
                document.add(paragraph);
            }

            document.close();
            return true;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}