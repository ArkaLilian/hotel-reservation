package org.example.exception;

public class ReservationNotFoundException extends Exception {
    public ReservationNotFoundException(int reservationId) {
        super("Réservation introuvable : aucune réservation avec l'id " + reservationId);
    }
}
