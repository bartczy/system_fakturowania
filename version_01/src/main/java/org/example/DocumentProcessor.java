package org.example;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.example.DocumentParser.parseOCR;

public class DocumentProcessor {

    public static void processScannedDocument(Path filePath) {
        System.out.println("Rozpoczynam analizę OCR pliku: " + filePath.toString());

        File imageFile = filePath.toFile();
        ITesseract tesseract = new Tesseract();

        // trzeba zainstalować program Tesseract-OCR (ważne żeby dodać też język poslki, podczas instalacji opcja "Additional language data") i podać tutaj ścieżkę do niego
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");

        // Ustawiamy język na polski
        tesseract.setLanguage("pol");
        // PSM 6 zakłada, że tekst to jeden spójny blok tekstu (często ratuje tabele)
        // Dostępna jest też opcja "4" ale póki co nie naprawiają problemu zjadania fragmentów
        tesseract.setPageSegMode(6);

        try {
            String extractedText = tesseract.doOCR(imageFile);

            System.out.println("================ WYNIK OCR ================");
            //System.out.println(extractedText);

            //Parsowanie stringa na zlecenia, pozycja = strona
            List<Zlecenie> zlecenia = parseOCR(extractedText);
            for (Zlecenie z : zlecenia){
                System.out.println(z.toString());
            }
            System.out.println("===========================================");


        } catch (TesseractException e) {
            System.err.println("Błąd podczas rozpoznawania tekstu: " + e.getMessage());
        }
    }
}
