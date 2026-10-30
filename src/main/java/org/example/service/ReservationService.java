package org.example.service;

import org.example.model.Reservation;
import org.example.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationService {
    private final Map<Integer, Room> rooms;
    private final List<Reservation> reservations;
    private int nextId;

    public ReservationService(Map<Integer, Room> rooms, List<Reservation> reservations) {
        if (rooms == null) {
            throw new IllegalArgumentException("Room ne peut pas être vide");
        }
        this.rooms = new HashMap<>(rooms); // copie défensive

        if (reservations == null) {
            throw new IllegalArgumentException("Reservation ne peut pas être vide");
        }
        this.reservations = new ArrayList<>(reservations); // copie défensive

        // plus grand id existant + 1 (liste vide : max = 0, donc on commence à 1)
        this.nextId = this.reservations.stream().mapToInt(Reservation::getId).max().orElse(0) + 1;
    }
}
