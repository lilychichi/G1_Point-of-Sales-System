module controller {

    // JavaFX Requirements
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base; // Added this one as it's often needed for basic FX operations

    // JDBC/MySQL Requirements
    requires java.sql; // Essential for all JDBC operations

    // Tells Java to link the MySQL driver JAR.
    // This is required because you added it to your pom.xml.
    requires mysql.connector.j;

    requires itextpdf;

    // Allows JavaFX runtime to access your controller classes via FXML
    opens controller to javafx.fxml;

    // Allows the database driver (via java.sql) to be loaded at runtime.
    // This makes the Class.forName("com.mysql.cj.jdbc.Driver") call work.

    // Makes your classes accessible to other parts of the application
    exports controller;
}
