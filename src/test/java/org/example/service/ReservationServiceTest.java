package org.example.service;

import org.example.exception.ReservationNotFoundException;
import org.example.exception.RoomNotAvailableException;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {
    private ReservationService reservationService;
    private Customer alice;
    private LocalDate today;
    private Room room01;
    private Room room02;
    private Room room03;

    @BeforeEach
    public void setUp() {
        alice = new Customer(1,"Alice","Martin", "alice.martin@mail.com");
        today = LocalDate.now(); // On met la date d'aujourd'hui
        room01 = new StandardRoom(1,2,80);
        room02 = new StandardRoom(2,2,80);
        room03 = new Suite(3,4,130);

        Reservation confirmed = new Reservation(1, alice, room01, today.plusDays(10), today.plusDays(14), 2, ReservationStatus.CONFIRMED);
        Reservation cancelled = new Reservation(2, alice, room02, today.plusDays(10), today.plusDays(14), 2, ReservationStatus.CANCELLED);

        Map<Integer, Room> rooms = Map.of(1, room01, 2, room02, 3, room03);
        reservationService = new ReservationService(rooms, List.of(confirmed, cancelled));
    }

    @Test void shouldBookFreeRoom() throws RoomNotAvailableException {
        Reservation booked = reservationService.book(alice, 3, today.plusDays(10), today.plusDays(12), 2);
        assertEquals(room03, booked.getRoom());
        assertEquals(ReservationStatus.CONFIRMED, booked.getStatus());
        assertEquals(3, reservationService.getReservations().size());
    }

    // La chambre 1 est occupée de J+10 à J+14 : J+12 → J+16 tombe dedans
    @Test void shouldRejectBookingWhenDatesOverlap() { assertThrows(RoomNotAvailableException.class, () -> reservationService.book(alice, 1, today.plusDays(12), today.plusDays(16), 2)); }

    // Bord à bord : arriver le jour où l'autre client part n'est pas un conflit
    @Test void shouldAcceptBookingStartingOnCheckOutDay() throws RoomNotAvailableException { assertNotNull(reservationService.book(alice, 1, today.plusDays(14), today.plusDays(17), 2)); }

    // La réservation de la chambre 2 est annulée, ses dates doivent être libres
    @Test void shouldAcceptBookingOnDatesOfCancelledReservation() throws RoomNotAvailableException { assertNotNull(reservationService.book(alice, 2, today.plusDays(10), today.plusDays(14), 2)); }

    // On vérifie le message pour être sûr que c'est bien la capacité qui bloque
    @Test void shouldRejectBookingWhenTooManyGuests() {
        var e = assertThrows(IllegalArgumentException.class, () -> reservationService.book(alice, 1, today.plusDays(20), today.plusDays(22), 3));
        assertTrue(e.getMessage().contains("accueillir"));
    }

    // Même vérification du message : il faut que ce soit la chambre inconnue qui bloque
    @Test void shouldRejectBookingWhenRoomDoesNotExist() {
        var e = assertThrows(IllegalArgumentException.class, () -> reservationService.book(alice, 999, today.plusDays(20), today.plusDays(22), 2));
        assertTrue(e.getMessage().contains("n'existe pas"));
    }

    @Test void shouldThrowWhenCancellingUnknownId() { assertThrows(ReservationNotFoundException.class, () -> reservationService.cancel(999)); }

    // Pendant J+11 → J+13, la chambre 1 est occupée ; la 2 est libre car sa réservation est annulée ; pour 3 personnes seule la suite convient
    @Test void shouldExcludeBusyAndTooSmallRooms() {
        assertEquals(List.of(room02, room03), reservationService.findAvailableRooms(today.plusDays(11), today.plusDays(13), 2));
        assertEquals(List.of(room03), reservationService.findAvailableRooms(today.plusDays(11), today.plusDays(13), 3));
    }
}
