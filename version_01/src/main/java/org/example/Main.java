package org.example;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {

        // Zamiast męczyć się z łączeniem skanera używamy "hot directory" czyli nasłuchujemy dodania nowych plików i automatycznie je wsadzamy do programu
        // Z perspektywy klienta będzie to oznaczać że muszą odpalić aplikację a następnie skanować do odpowiedniego folderu a aplikacja zrobi resztę
        Path scanFolder = Paths.get("C:\\Users\\bartek\\Documents\\projekt_zespolowy\\faktury_test");

        try {
            // Upewniamy się, że folder istnieje
            if (!Files.exists(scanFolder)) {
                Files.createDirectories(scanFolder);
                System.out.println("Utworzono folder nasłuchu: " + scanFolder);
            }

            // Tworzymy usługę nasłuchującą
            WatchService watchService = FileSystems.getDefault().newWatchService();
            scanFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            System.out.println("Uruchomiono system. Czekam na nowe skany w: " + scanFolder + "...");

            // Nieskończona pętla, która czeka na pliki (wersja robocza)
            while (true) {
                WatchKey key = watchService.take(); // Blokuje działanie do momentu pojawienia się pliku

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path newFileName = (Path) event.context();
                        Path fullFilePath = scanFolder.resolve(newFileName);

                        System.out.println("\n[!] Wykryto nowy dokument ze skanera!");
                        System.out.println("Nazwa pliku: " + newFileName);

                        // TUTAJ DODAMY LOGIKĘ CZYTANIA TEKSTU (OCR)
                        processScannedDocument(fullFilePath);
                    }
                }

                // Resetujemy klucz, aby czekać na kolejne pliki
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas nasłuchiwania folderu: " + e.getMessage());
        }
    }

    // Metoda, która zajmie się plikiem, gdy już zostanie wykryty
    private static void processScannedDocument(Path filePath) {
        System.out.println("Rozpoczynam analizę pliku: " + filePath.toString());
        DocumentProcessor.processScannedDocument(filePath);
    }
}