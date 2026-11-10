package org.example.exception;

import org.example.utils.DateFormats;

import java.time.LocalDate;

public class RoomNotAvailableException extends Exception{
    public RoomNotAvailableException(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        super("Réservation impossible : la chambre " + roomNumber + " est déjà prise entre le " + checkIn.format(DateFormats.FRENCH) + " et le " + checkOut.format(DateFormats.FRENCH));
    }
}
