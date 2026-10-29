package org.example.exception;

import java.time.LocalDate;

public class RoomNotAvailableException extends Exception{
    public RoomNotAvailableException(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        super("Réservation impossible : la chambre " + roomNumber + " est déjà prise entre le " + checkIn + " et le " + checkOut);
    }
}
