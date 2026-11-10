package org.example.utils;

import org.example.model.Room;
import org.example.model.StandardRoom;
import org.example.model.Suite;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoomFactoryTest {

    // Le type du CSV doit donner la bonne sous classe, sinon le prix par nuit serait faux
    @Test void shouldCreateStandardRoom() { assertTrue(RoomFactory.create("STANDARD", 101, 2, 80.0) instanceof StandardRoom); }

    @Test void shouldCreateSuite() { assertTrue(RoomFactory.create("SUITE", 201, 4, 180.0) instanceof Suite); }

    // Limite : grace au toUpperCase une faute de casse dans le CSV ne bloque pas la ligne
    @Test void shouldAcceptLowerCaseType() {
        Room room = RoomFactory.create("suite", 202, 2, 150.0);
        assertTrue(room instanceof Suite);
    }

    // Type inconnu : le message doit contenir le type pour savoir quelle ligne du CSV corriger
    @Test void shouldRejectUnknownType() {
        var e = assertThrows(IllegalArgumentException.class, () -> RoomFactory.create("DELUXE", 301, 2, 200.0));
        assertTrue(e.getMessage().contains("DELUXE"));
    }
}
