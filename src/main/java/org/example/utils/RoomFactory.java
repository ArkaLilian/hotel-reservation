package org.example.utils;

import org.example.model.Room;
import org.example.model.StandardRoom;
import org.example.model.Suite;

public class RoomFactory {

    // Seul endroit qui fait new StandardRoom / new Suite : un nouveau type de chambre ne touche que ce switch.
    // Switch remplace une série de if else
    public static Room create(String type, int number, int capacity, double basePrice) {
        return switch (type.toUpperCase()) {
            case "STANDARD" -> new StandardRoom(number, capacity, basePrice);
            case "SUITE" -> new Suite(number, capacity, basePrice);
            default -> throw new IllegalArgumentException("Type de chambre inconnu : " + type);
        };
    }
}
