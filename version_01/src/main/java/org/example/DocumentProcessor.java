package org.example;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;
import java.nio.file.Path;

public class DocumentProcessor {

    public static void processScannedDocument(Path filePath) {
        System.out.println("Rozpoczynam analizę OCR pliku: " + filePath.toString());

        File imageFile = filePath.toFile();
        ITesseract tesseract = new Tesseract();

        // trzeba zainstalować program Tesseract-OCR (ważne żeby dodać też język poslki, podczas instalacji opcja "Additional language data") i podać tutaj ścieżkę do niego
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");

        // Ustawiamy język na polski
        tesseract.setLanguage("pol");

        try {
            String extractedText = tesseract.doOCR(imageFile);

            System.out.println("================ WYNIK OCR ================");
            System.out.println(extractedText);
            System.out.println("===========================================");

            // TODO Dane są w formie wielkiego stringa który trzeba będzie parsować gdy dostaniemy kartki
            // parseDataFromText(extractedText);

        } catch (TesseractException e) {
            System.err.println("Błąd podczas rozpoznawania tekstu: " + e.getMessage());
        }
    }
}
