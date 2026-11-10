package org.example;

import org.example.model.Customer;
import org.example.model.Reservation;
import org.example.model.Room;
import org.example.service.LongStayDiscount;
import org.example.service.PricingService;
import org.example.service.ReservationService;
import org.example.utils.CsvLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Main {
    // Main ne fait que l'assemblage : c'est le seul endroit où on fait new des services, tout le reste les reçoit par le constructeur
    static void main() {
        CsvLoader loader = new CsvLoader();
        Map<Integer, Room> rooms;
        Map<Integer, Customer> customers;
        List<Reservation> reservations;

        // Ordre imposé : les réservations ont besoin des chambres et des clients deja chargés
        try {
            rooms = loader.loadRooms(Path.of("data", "rooms.csv"));
            customers = loader.loadCustomers(Path.of("data", "customers.csv"));
            reservations = loader.loadReservations(Path.of("data", "reservations.csv"), rooms, customers);
        } catch (IOException e) {
            System.err.println("Impossible de lire le fichier " + e.getMessage() + " : lancez l'application depuis la racine du projet (dossier data/).");
            return;
        }

        System.out.println(rooms.size() + " chambres, " + customers.size() + " clients et " + reservations.size() + " réservations chargés.");
        for (String warning : loader.getWarnings()) {
            System.out.println("Attention : " + warning);
        }

        PricingService pricingService = new PricingService(List.of(new LongStayDiscount()));
        ReservationService reservationService = new ReservationService(rooms, reservations);
        new ConsoleMenu(reservationService, pricingService, customers).run();
    }
}
