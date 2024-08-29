package com.tsb.service;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;


public class DocumentService extends PDFStreamEngine {
    public PDDocument loadPDFFile(String fileName) {
        try (PDDocument document = PDDocument.load(new File(fileName))) {
            document.close();
            return document;
        } catch (IOException e) {
            throw new RuntimeException("loadPDFFile " + e);
        }
    }

    public String getEDocumentDesignation(String fileName) {
        return getFileName(fileName).replaceFirst("[.][^.]+$", "");
    }

    public String getFileName(String fileName) {
        return new File(fileName).getName();
    }

    public String getDocumentName(String fileName) {
        String text = reduceText(fileName);
        String element = getProjectCode(fileName);
        int endIndex = text.indexOf(element);
        return text.substring(0, endIndex);
    }

    public String getProjectCode(String fileName) {
        String text = reduceText(fileName);
        Pattern pattern = Pattern.compile("\\d{3,}");
        Matcher matcher = pattern.matcher(text);
        int fromIndex = 0;
        if (matcher.find()) {
            fromIndex = matcher.start();
        } else {
            System.out.println("No match found.");
        }
        return text.substring(fromIndex).trim();
    }

    public String getPDFModificationDate(String fileName, String pattern) {
        Date date = doPdfModificationDate(fileName);
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    // get sumSizePages in mm
    public double sumSizePages(String fileName) {
        try (PDDocument document = loadPDFFile(fileName)) {
            double size = 0.0;
            for (PDPage page : document.getPages()) {
                size += getSizeSheet(page);
            }
            document.close();
            return size;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double getSizeSheet(PDPage page) {
        double height = page.getMediaBox().getHeight() * 25.4 / 72;
        double width = page.getMediaBox().getWidth() * 25.4 / 72;
        return height * width;
    }

    public int countPagesAFour(String fileName) {
        int aFour = 62370;
        return (int) (sumSizePages(fileName) / aFour);
    }

    public double countPagesAOne(String fileName) {
        double aOne = 8;
        return countPagesAFour(fileName) / aOne;
    }

    public int getNumberPages(String fileName) {
        int numberPages;
        try (PDDocument document = loadPDFFile(fileName)) {
            numberPages = document.getNumberOfPages();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return numberPages;
    }

    public String checksumForDigest(String filename) throws IOException, NoSuchAlgorithmException {
        var messageDigest = MessageDigest.getInstance("MD5");
        try (var fis = new FileInputStream(filename);
             var bis = new BufferedInputStream(fis);
             var dis = new DigestInputStream(bis, messageDigest)) {

            while (dis.read() != -1) ;
            messageDigest = dis.getMessageDigest();
        }
        var result = new StringBuilder();
        for (byte b : messageDigest.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public String getCrc32(String filename) {
        try (
                var fis = new FileInputStream(filename);
                var bis = new BufferedInputStream(fis);
                var cis = new CheckedInputStream(bis, new CRC32())
        ) {
            while (cis.read() >= 0) ;
            return Long.toHexString(cis.getChecksum().getValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPdfFileSize(String fileName) {
        File file = new File(fileName);// fileName
        return new DecimalFormat("###,###").format(file.length());
    }

    public String extractText(String fileName) {
        String text = null;
        try (PDDocument doc = PDDocument.load(new File(fileName))) {
            PDDocument document = new PDDocument();
            document.addPage(doc.getDocumentCatalog().getPages().get(0));
            document.close();
            text = new PDFTextStripper().getText(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    // Имя файла без расширения  https://for-each.dev/lessons/b/-java-filename-without-extension
    public static String removeFileExtension(String filename, boolean removeAllExtensions) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }
        String extPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");
        return filename.replaceAll(extPattern, "");
    }

    private String reduceText(String fileName) {
        String text = extractTextToLine(fileName);
        int startIndex = text.indexOf("Раздел");
        int toIndex = text.indexOf("Том");
        return text.substring(startIndex, toIndex);
    }

    private String extractTextToLine(String fileName) {
        return extractText(fileName).replaceAll("\r", "")
                .replaceAll("\n", " ")
                .replaceAll("\\s{2,}", " ");
    }

    private Date doPdfModificationDate(String fileName) {
        try (PDDocument document = loadPDFFile(fileName)) {
            PDDocumentInformation info = document.getDocumentInformation();
            document.close();
            return info.getModificationDate().getTime();
        } catch (IOException e) {
            throw new RuntimeException("pDFModificationDate " + e);
        }
    }
}
