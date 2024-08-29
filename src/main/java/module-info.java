module com.tsb.identitysheet {
    requires javafx.controls;
    requires javafx.fxml;
    requires spire.doc.free;
    requires org.apache.pdfbox;


    opens com.tsb.sample to javafx.fxml;
    exports com.tsb.sample;
}