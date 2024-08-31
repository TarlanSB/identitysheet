package com.tsb.sample;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.tsb.service.DocumentService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private List<String> fileExtensionList;

    @FXML
    private Label labelSingleFile;

    @FXML
    private RadioButton buttonCrc32;

    @FXML
    private ToggleGroup groupCheckSum;

    @FXML
    private RadioButton buttonMd5;

    @FXML
    private TextField checkSum;

    @FXML
    private TextField documentName;

    @FXML
    private TextField eDocumentDesignation;

    @FXML
    private TextField fileName;

    @FXML
    private TextField fileSize;

    @FXML
    private TextField numberPages;

    @FXML
    private TextField pagesAFour;

    @FXML
    private TextField pagesAOne;

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
    private TextField projectCode;

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
    void copyProjectCode() {
        doCopy(projectCode.getText());
    }


    static String filePath = null;
    DocumentService documentService = new DocumentService();

//    MSDocumentService msDocumentService = new MSDocumentService();

    FileChooser fileChooser = new FileChooser();

    @FXML
    String fileChooser() {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File extension", fileExtensionList));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            cleanForm1();
            filePath = file.getAbsolutePath();
            labelSingleFile.setText("Путь: " + filePath);
            eDocumentDesignation.setText(documentService.getEDocumentDesignation(filePath));
            documentName.setText(documentService.getDocumentName(filePath));
            numberPages.setText(String.valueOf(documentService.getNumberPages(filePath)));
            pagesAFour.setText(String.valueOf(documentService.countPagesAFour(filePath)));
            pagesAOne.setText(String.valueOf(documentService.countPagesAOne(filePath)));
            fileName.setText(documentService.getFileName(filePath));
            fileSize.setText(documentService.getPdfFileSize(filePath));
            projectCode.setText(documentService.getProjectCode(filePath));
            fileChooser.setInitialDirectory(new File(file.getParent()));
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
    public void choiceCheckSum() {
        getCheckSum(documentService);
    }

    static String pattern = "";

    @FXML
    void getPatternDate() {
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
    void saveIdentitySheet() {

        Document document = new Document("src/main/resources/com/tsb/template-IS.docx");

        document.replace("$electronicDocumentDesignation", eDocumentDesignation.getText(), false, true);
        document.replace("$documentName", documentName.getText(), false, true);
        document.replace("$countPagesAOne", pagesAOne.getText(), false, true);
        document.replace("$md5Crc32", checkSumName, false, true);
        document.replace("$checkSum", checkSum.getText(), false, true);
        document.replace("$getFileName", fileName.getText(), false, true);
        document.replace("$getPDFModificationDate", modificationDate.getText(), false, true);
        document.replace("$pdfFileSize", fileSize.getText(), false, true);
        document.replace("$drawn", drawn.getText(), false, true);
        document.replace("$chkdBy", chkdBy.getText(), false, true);
        document.replace("$departmentChief", departmentChief.getText(), false, true);
        document.replace("$regDocCrl", regDocCrl.getText(), false, true);
        document.replace("$generalEngineer", generalEngineer.getText(), false, true);
        document.replace("$director", director.getText(), false, true);
        document.replace("$dateSign", dateSign.getText(), false, true);
        document.replace("$projectCode", projectCode.getText() + "-УЛ", false, true);

        String identityDocName = DocumentService.removeFileExtension(filePath, true) + "-ИУЛ.docx";

        //Save the result document
        document.saveToFile(identityDocName, FileFormat.Docx_2013);

        //msDocumentService.replaceText("C:\\Users\\WINDOW\\OneDrive\\Desktop\\Разработка ИУЛ\\Новая папка\\input.doc");
    }

    String clearText = "";

    @FXML
    void cleanForm1() {
        if (!labelSingleFile.getText().equals("")) {
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
            projectCode.setText(clearText);
        }
    }

    @FXML
    void cleanForm2() {
        drawn.setText(clearText);
        chkdBy.setText(clearText);
        departmentChief.setText(clearText);
        regDocCrl.setText(clearText);
        generalEngineer.setText(clearText);
        director.setText(clearText);
        dateSign.setText(clearText);
    }

    @FXML
    void cleanAll() {
        cleanForm2();
        cleanForm1();
    }
}