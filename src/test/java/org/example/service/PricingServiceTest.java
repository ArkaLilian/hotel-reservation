package org.example.service;

import org.example.model.*;
import org.junit.jupiter.api.BeforeEach; // Pour que le setUp soit exécuté en premier voir cours prof
import org.junit.jupiter.api.Test; // Pour les test Obligatoire cours prof aussi

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PricingServiceTest {
    private static final double DELTA = 0.001; // tolérance pour comparer des double
    private Customer customer;
    private Room standard; // 100 € la nuit
    private Room suite;    // 100 + 30 de supplément = 130 € la nuit
    private PricingService service;

    @BeforeEach
    void setUp() {
        customer = new Customer(1, "Jean", "Dupont", "jean.dupont@mail.com");
        standard = new StandardRoom(101, 2, 100.0);
        suite = new Suite(201, 4, 100.0);
        service = new PricingService(List.of(new LongStayDiscount()));
    }

    // Réservation de tant de `nights` (nuits) à partir du 1er décembre
    private Reservation booking(Room room, int nights) {
        LocalDate start = LocalDate.of(2026, 12, 1);
        return new Reservation(1, customer, room, start, start.plusDays(nights), 2, ReservationStatus.CONFIRMED);
        // Le start.plusDays(nigths) c'est pour calculer la date de fin du séjour avec le nombre de nuit pour faire plusieurs test avec un seul constructeur
    }

    // Ici on a un Séjour court : pas de remise, prix = nuits x tarif (chambre classique)
    @Test void shortStayHasNoDiscount() { assertEquals(300.0, service.computePrice(booking(standard, 3)), DELTA); }
    // La on teste avec une limite basse : 6 nuits ne déclenchent pas encore la remise donc on vérifie le prix
    @Test void sixNightsHasNoDiscount() { assertEquals(600.0, service.computePrice(booking(standard, 6)), DELTA); }
    // Tout pile la Limite maintenant : 7 nuits pile déclenchent la remise de 10 % (700 x 0,9)
    @Test void sevenNightsGetsTenPercentOff() { assertEquals(630.0, service.computePrice(booking(standard, 7)), DELTA); }
    // Polymorphisme room classe abstraire on 'extends' la classe : même durée, la suite coûte plus cher (130 x 3)
    @Test void suiteCostsMoreThanStandard() { assertEquals(390.0, service.computePrice(booking(suite, 3)), DELTA); }
    // Aucune règle : on obtient le prix de base sans remise, même à 7 nuits
    @Test void noRulesReturnsBasePrice() {
        PricingService noRules = new PricingService(List.of());
        assertEquals(700.0, noRules.computePrice(booking(standard, 7)), DELTA);
    }

    // Arguments invalides : test juste simples pour les nulls Rules et Reservations.
    @Test void nullRulesRejected() { assertThrows(IllegalArgumentException.class, () -> new PricingService(null)); }
    @Test void nullReservationRejected() { assertThrows(IllegalArgumentException.class, () -> service.computePrice(null)); }
}
