module com.example.travelagencyapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.controlsfx.controls;

    opens users to com.google.gson;
    opens venueData to com.google.gson;
    opens dataStorage to com.google.gson;
    opens Universal to com.google.gson;

    opens gui to javafx.fxml, org.controlsfx.controls;
    exports gui;
}