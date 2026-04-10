package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentParser {

    public static List<Zlecenie> parseOCR(String ocrText) {
        List<Zlecenie> wszystkieZlecenia = new ArrayList<>();
        Zlecenie aktualneZlecenie = null;

        String[] lines = ocrText.split("\\r?\\n");
        Pattern datePattern = Pattern.compile("DATA:\\s*(\\d{2}\\.\\d{2}\\.\\d{4})");

        // Elastyczny Regex:
        // 1. (.*?) -> Nazwa (bierze wszystko na początku)
        // 2. ([a-zA-Z0-9]{4}\\s*s[a-z]*r)? -> Opcjonalny RAL (4 znaki + warianty "str", "sr")
        // 3. szt -> słowo klucz
        // 4. ([0-9sSoO]+) -> Ilość (cyfry, ale też litery 's' czy 'o', w które OCR zmienia cyfry 5 i 0)
        Pattern itemPattern = Pattern.compile("^(.*?)(?:\\||\\s)*([a-zA-Z0-9]{4}\\s*s[a-z]*r)?(?:\\||\\s)*szt(?:\\||\\s)*([0-9sSoO]+)");

        for (String line : lines) {
            Matcher dateMatcher = datePattern.matcher(line);
            if (dateMatcher.find()) {
                aktualneZlecenie = new Zlecenie();
                aktualneZlecenie.data = dateMatcher.group(1);
                wszystkieZlecenia.add(aktualneZlecenie);
                continue;
            }

            if (aktualneZlecenie != null && line.contains("szt")) {
                // Wstępne czyszczenie linii z krzaków Tesseracta
                String cleanLine = line.replaceAll("[\\[\\]_\\)/]+", " ").replaceAll("\\|+", "|");

                Matcher itemMatcher = itemPattern.matcher(cleanLine);
                if (itemMatcher.find()) {
                    // Czyszczenie nazwy (Usuwamy numerację z początku i zbędne rury)
                    String nazwa = itemMatcher.group(1).replaceAll("^\\s*\\d+\\s*\\|?\\s*", "").replace("|", "").trim();

                    // Czyszczenie RAL (Jeśli znalazł)
                    String rawRal = itemMatcher.group(2);
                    String ral = "";
                    if (rawRal != null) {
                        // Tesseract często czyta zera jako 'o' i jedynki jako 'l' w kodach RAL
                        ral = rawRal.substring(0, 4)
                                .toLowerCase()
                                .replace("o", "0")
                                .replace("l", "1")
                                .replace("a", "1");
                    }

                    // Czyszczenie ilości (Zamiana najczęstszych błędów literowych na cyfry)
                    String rawIlosc = itemMatcher.group(3);
                    int ilosc = 0;
                    try {
                        String naprawionaIlosc = rawIlosc.toLowerCase()
                                .replace("s", "5")
                                .replace("o", "0");
                        ilosc = Integer.parseInt(naprawionaIlosc);
                    } catch (NumberFormatException e) {
                        ilosc = 1; // Wartość domyślna w razie kryzysu
                    }

                    if (!nazwa.isEmpty()) {
                        aktualneZlecenie.pozycje.add(new PozycjaFaktury(nazwa, ral, ilosc));
                    }
                }
            }
        }
        return wszystkieZlecenia;
    }
}