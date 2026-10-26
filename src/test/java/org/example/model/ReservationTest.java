package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationTest {
    private Customer customer;
    private Room room;
    private Reservation reservation; // du 20 au 24 : 4 nuits

    @BeforeEach
    void setUp() {
        customer = new Customer(1, "Jean", "Dupont", "jean.dupont@mail.com");
        room = new StandardRoom(101, 2, 80.0);
        reservation = new Reservation(1, customer, room, LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 24), 2, ReservationStatus.CONFIRMED);
    }
    // On va tester que la réservation est bien de 4 nuits
    @Test void nights()                    { assertEquals(4, reservation.getNights()); }
    // On va tester une reservation False (pas de chevauchement) avant et après et les dates de Jean Dupont
    @Test void noOverlapBefore()           { assertFalse(reservation.overlaps(LocalDate.of(2026, 12, 10), LocalDate.of(2026, 12, 15))); }
    @Test void noOverlapAfter()            { assertFalse(reservation.overlaps(LocalDate.of(2026, 12, 28), LocalDate.of(2026, 12, 31))); }
    // On va tester une reservation Dans le chackin, puis Check out et aussi entre les deux puis a l'exterieur des deux dates de Checkint et checkout
    @Test void overlapAtCheckIn()          { assertTrue(reservation.overlaps(LocalDate.of(2026, 12, 18), LocalDate.of(2026, 12, 22))); }
    @Test void overlapAtCheckOut()         { assertTrue(reservation.overlaps(LocalDate.of(2026, 12, 22), LocalDate.of(2026, 12, 27))); }
    @Test void overlapInside()             { assertTrue(reservation.overlaps(LocalDate.of(2026, 12, 21), LocalDate.of(2026, 12, 23))); }
    @Test void overlapAround()             { assertTrue(reservation.overlaps(LocalDate.of(2026, 12, 15), LocalDate.of(2026, 12, 30))); }
    // Ici plus compliqué on test les effets de bords de notre chevauchement un client peut partir et un autre arriver dans la chambre le meme jour
    // donc on vérifie que sur un depart le 20 et arrivée le 24 d'un autre client ca ne chevauche pas la résa de Jean d'ou le False
    @Test void noOverlapStartsOnCheckOut() { assertFalse(reservation.overlaps(LocalDate.of(2026, 12, 24), LocalDate.of(2026, 12, 27))); }
    @Test void noOverlapEndsOnCheckIn()    { assertFalse(reservation.overlaps(LocalDate.of(2026, 12, 17), LocalDate.of(2026, 12, 20))); }
    //On vérifie qu'on ne peut pas crée une réservation de 0 nuits (Checkin et Checkout le meme jour)
    @Test
    void sameDayRejected() {
        LocalDate day = LocalDate.of(2026, 12, 20);
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation(2, customer, room, day, day, 2, ReservationStatus.CONFIRMED));
    }

    // On vérifie qu'on ne peut pas créer une réservation où le Checkout est avant le Checkin
    @Test
    void checkOutBeforeRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation(2, customer, room, LocalDate.of(2026, 12, 24), LocalDate.of(2026, 12, 20), 2, ReservationStatus.CONFIRMED));
    }

    // On vérifie qu'on ne peut pas créer une réservation sans client (null)
    @Test
    void nullCustomerRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation(2, null, room, LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 24), 2, ReservationStatus.CONFIRMED));
    }

    // On vérifie qu'on ne peut pas créer une réservation avec 0 personne
    @Test
    void zeroGuestsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation(2, customer, room, LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 24), 0, ReservationStatus.CONFIRMED));
    }

    // On vérifie que cancel() passe le statut à CANCELLED et que la réservation n'est plus active
    @Test
    void cancelSetsStatus() {
        reservation.cancel();

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertFalse(reservation.isActive());
    }

    // On vérifie qu'on ne peut pas annuler deux fois la même réservation
    @Test
    void cancelTwiceRejected() {
        reservation.cancel();

        assertThrows(IllegalStateException.class, () -> reservation.cancel());
    }
}
