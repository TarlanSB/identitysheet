package com.tsb.sample;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.Table;
import com.tsb.service.DocumentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Controller implements Initializable {

    private List<String> fileExtensionList;

    @FXML
    private Label labelSingleFile;

    // getPDFModificationDate
    @FXML
    private RadioButton buttonCrc32;

    @FXML
    private ToggleGroup groupCheckSum;

    @FXML
    private RadioButton buttonMd5;

    @FXML
    private TextField checkSum;

    // getDocumentName
    @FXML
    private TextField documentName;

    // getEDocumentDesignation
    @FXML
    private TextField eDocumentDesignation;

    // getFileName
    @FXML
    private TextField fileName;

    //
    @FXML
    private TextField fileSize;

    @FXML
    private TextField numberPages;

    //
    @FXML
    private TextField pagesAFour;

    @FXML
    private TextField pagesAOne;

    // getPDFModificationDate
    @FXML
    private TextField modificationDate;

    @FXML
    private RadioButton buttonMonthNumberModDate;

    @FXML
    private RadioButton buttonMonthModDate;

    @FXML
    private ToggleGroup groupModificationDate;

    @FXML
    private TextField chkdBy;

    @FXML
    private TextField dateSign;

    @FXML
    private TextField departmentChief;

    @FXML
    private TextField director;

    @FXML
    private TextField drawn;

    @FXML
    private TextField generalEngineer;

    @FXML
    private TextField regDocCrl;

    @FXML
    void copyDocumentName() { // MouseEvent event
        doCopy(documentName.getText());
    }

    @FXML
    void copyEDocumentDesignation() {
        doCopy(eDocumentDesignation.getText());
    }

    @FXML
    void copyNumberPages() {
        doCopy(numberPages.getText());
    }

    @FXML
    void copyNumberPagesAFour() {
        doCopy(pagesAFour.getText());
    }

    @FXML
    void copyNumberPagesAOne() {
        doCopy(pagesAOne.getText());
    }

    @FXML
    void copyCheckSum() {
        doCopy(checkSum.getText());
    }

    @FXML
    void copyFileName() {
        doCopy(fileName.getText());
    }

    @FXML
    void copyFileSize() {
        doCopy(fileSize.getText());
    }

    @FXML
    void copyModificationDate() {
        doCopy(modificationDate.getText());
    }

    @FXML
    private Button buttonCleanForm1;


    static String filePath = "";
    DocumentService documentService = new DocumentService();

    FileChooser fileChooser = new FileChooser();

    @FXML
    String fileChooser(ActionEvent event) {

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File extension", fileExtensionList));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            filePath = file.getAbsolutePath();
            labelSingleFile.setText("Путь: " + filePath);
            eDocumentDesignation.setText(documentService.getEDocumentDesignation(filePath));
            documentName.setText(documentService.getDocumentName(filePath));
            numberPages.setText(String.valueOf(documentService.getNumberPages(filePath)));
            pagesAFour.setText(String.valueOf(documentService.countPagesAFour(filePath)));
            pagesAOne.setText(String.valueOf(documentService.countPagesAOne(filePath)));
            fileName.setText(documentService.getFileName(filePath));
            //getCheckSum(documentService, filePath);
            fileSize.setText(String.valueOf(documentService.getPdfFileSize(filePath)));
        }
        return Objects.requireNonNull(file).getAbsolutePath();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileExtensionList = new ArrayList<>();
        fileExtensionList.add("*.pdf");
    }

    private void doCopy(String fieldText) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(fieldText);
        clipboard.setContent(content);
    }

    static String checkSumName = "";

    @FXML
    void getCheckSum(DocumentService documentService) {
        String sum = "";
        if (groupCheckSum.getSelectedToggle().equals(buttonMd5)) {
            try {
                checkSumName = "MD5";
                sum = documentService.checksumForDigest(filePath);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            checkSum.setText(sum);
        } else if (groupCheckSum.getSelectedToggle().equals(buttonCrc32)) {
            checkSumName = "CRC32";
            sum = documentService.getCrc32(filePath);
        }
        checkSum.setText(sum);
    }

    @FXML
    public void choiceCheckSum(MouseEvent mouseEvent) {
        getCheckSum(documentService);
    }

    static String pattern = "";

    @FXML
    void getPatternDate(MouseEvent event) {
        String patternMonthModDate = "dd MMMM yyyy HH:mm:ss";
        String patternModDate = "dd.MM.yyyy HH:mm:ss";
        String date = "";
        if (groupModificationDate.getSelectedToggle().equals(buttonMonthNumberModDate)) {
            date = documentService.getPDFModificationDate(filePath, patternModDate);
            pattern = patternModDate;
        } else if (groupModificationDate.getSelectedToggle().equals(buttonMonthModDate)) {
            date = documentService.getPDFModificationDate(filePath, patternMonthModDate);
            pattern = patternMonthModDate;
        }
        modificationDate.setText(date);
    }

    @FXML
    void saveIdentitySheet(MouseEvent event) {
        Document document = new Document("src/main/resources/com/tsb/template-IS.docx");
        //Get the first section
        Section section = document.getSections().get(0);
        //Get the first table in the section
        Table table = section.getTables().get(0);
        //Create a map of values
        Map<String, String> map = new HashMap<>();
        map.put("$electronicDocumentDesignation", eDocumentDesignation.getText());
        map.put("$documentName", documentName.getText());
        map.put("$countPagesAOne", pagesAOne.getText());
        map.put("$md5Crc32", checkSumName);
        map.put("$checkSum", checkSum.getText());
        map.put("$getFileName", fileName.getText());
        map.put("$getPDFModificationDate", modificationDate.getText());
        map.put("$pdfFileSize", fileSize.getText());
        map.put("$drawn", drawn.getText());
        map.put("$chkdBy", chkdBy.getText());
        map.put("$departmentChief", departmentChief.getText());
        map.put("$regDocCrl", regDocCrl.getText());
        map.put("$generalEngineer", generalEngineer.getText());
        map.put("$director", director.getText());
        map.put("$dateSign", dateSign.getText());
        //Replace text in the table
        for (Map.Entry<String, String> entry : map.entrySet()) {
            table.replace(entry.getKey(), entry.getValue(), false, true);
        }
        String identityDocName = DocumentService.removeFileExtension(filePath, true) + "-ИУЛ.docx";
        //Save the result document
        document.saveToFile(identityDocName, FileFormat.Docx_2013);
    }

    @FXML
    void cleanForm1(MouseEvent event) {
      //  filePath = file.getAbsolutePath();
        String clearText = "";
        labelSingleFile.setText("");
        eDocumentDesignation.setText(clearText);
        documentName.setText(clearText);
        numberPages.setText(clearText);
        pagesAFour.setText(clearText);
        pagesAOne.setText(clearText);
        fileName.setText(clearText);
        checkSum.setText(clearText);
        modificationDate.setText(clearText);
        fileSize.setText(clearText);
    }
}