module com.tsb.identitysheet {
    requires javafx.controls;
    requires javafx.fxml;
    requires pdfbox;
    requires spire.doc.free;


    opens com.tsb.sample to javafx.fxml;
    exports com.tsb.sample;
}