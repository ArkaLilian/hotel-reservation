package org.example.utils;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

// Format des dates que voit l'utilisateur (saisie et affichage). Les CSV restent en AAAA-MM-JJ, le format que LocalDate.parse lit directement
public class DateFormats {
    // STRICT sinon un 31/02/2026 serait corrigé en silence en 28/02/2026 au lieu d'être refusé (et STRICT oblige a mettre uuuu au lieu de yyyy)
    public static final DateTimeFormatter FRENCH = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
}
