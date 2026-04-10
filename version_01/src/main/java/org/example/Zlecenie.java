package org.example;

import java.util.ArrayList;
import java.util.List;

class Zlecenie {
    public String data;
    public List<PozycjaFaktury> pozycje = new ArrayList<>();

    @Override
    public String toString() {
        return "Zlecenie z dnia: " + data + "\nPozycje:\n" + pozycje;
    }
}

class PozycjaFaktury {
    public String nazwa;
    public String ral;
    public int ilosc;

    public PozycjaFaktury(String nazwa, String ral, int ilosc) {
        this.nazwa = nazwa;
        this.ral = ral;
        this.ilosc = ilosc;
    }

    @Override
    public String toString() {
        return "- " + nazwa + " (RAL: " + ral + ") x" + ilosc + " szt.";
    }
}