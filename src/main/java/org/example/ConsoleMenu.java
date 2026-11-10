package org.example;

import org.example.exception.ReservationNotFoundException;
import org.example.exception.RoomNotAvailableException;
import org.example.model.Customer;
import org.example.model.Reservation;
import org.example.model.Room;
import org.example.service.PricingService;
import org.example.service.ReservationService;
import org.example.utils.DateFormats;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

// Seule classe qui lit le clavier et affiche : les services ne font que calculer ou lancer des exceptions
public class ConsoleMenu {
    private final ReservationService reservationService;
    private final PricingService pricingService;
    private final Map<Integer, Customer> customers;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(ReservationService reservationService, PricingService pricingService, Map<Integer, Customer> customers) {
        this.reservationService = reservationService;
        this.pricingService = pricingService;
        this.customers = Map.copyOf(customers);
    }

    public void run() {
        int choice = -1;
        while (choice != 0) {
            showMenu();
            choice = readInt("Votre choix : ");
            // Un seul try/catch pour toutes les options : une erreur métier affiche son message et on revient au menu
            try {
                switch (choice) {
                    case 1 -> showRooms();
                    case 2 -> searchAvailableRooms();
                    case 3 -> bookRoom();
                    case 4 -> cancelReservation();
                    case 5 -> showCustomerReservations();
                    case 0 -> System.out.println("Au revoir !");
                    default -> System.out.println("Choix invalide, tapez un nombre entre 0 et 5.");
                }
            } catch (RoomNotAvailableException | ReservationNotFoundException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void showMenu() {
        System.out.println();
        System.out.println("===== Hôtel : réservations =====");
        System.out.println("1. Lister toutes les chambres");
        System.out.println("2. Chercher les chambres libres");
        System.out.println("3. Réserver une chambre");
        System.out.println("4. Annuler une réservation");
        System.out.println("5. Voir les réservations d'un client");
        System.out.println("0. Quitter");
    }

    private void showRooms() {
        for (Room room : reservationService.getRooms()) {
            printRoom(room);
        }
    }

    private void searchAvailableRooms() {
        LocalDate start = readDate("Date d'arrivée (JJ/MM/AAAA) : ");
        LocalDate end = readDate("Date de départ (JJ/MM/AAAA) : ");
        int guests = readInt("Nombre de personnes : ");

        List<Room> availableRooms = reservationService.findAvailableRooms(start, end, guests);
        if (availableRooms.isEmpty()) {
            System.out.println("Aucune chambre libre pour ces dates.");
        }
        for (Room room : availableRooms) {
            printRoom(room);
        }
    }

    // Le prix est calculé ici et pas dans ReservationService : les deux services restent indépendants
    private void bookRoom() throws RoomNotAvailableException {
        Customer customer = askCustomer();
        int roomNumber = readInt("Numéro de la chambre : ");
        LocalDate start = readDate("Date d'arrivée (JJ/MM/AAAA) : ");
        LocalDate end = readDate("Date de départ (JJ/MM/AAAA) : ");
        int guests = readInt("Nombre de personnes : ");

        Reservation reservation = reservationService.book(customer, roomNumber, start, end, guests);
        System.out.println("Réservation confirmée :");
        printReservation(reservation);
    }

    private void cancelReservation() throws ReservationNotFoundException {
        int id = readInt("Numéro de la réservation à annuler : ");
        reservationService.cancel(id);
        System.out.println("La réservation n°" + id + " est annulée, ses dates sont de nouveau libres.");
    }

    private void showCustomerReservations() {
        Customer customer = askCustomer();
        List<Reservation> reservations = reservationService.findByCustomer(customer);
        if (reservations.isEmpty()) {
            System.out.println(customer.getFullName() + " n'a aucune réservation.");
        }
        for (Reservation reservation : reservations) {
            printReservation(reservation);
        }
    }

    // On affiche les clients avant de demander l'id, sinon l'utilisateur ne peut pas le deviner
    private Customer askCustomer() {
        customers.values().stream()
                .sorted(Comparator.comparingInt(Customer::getId))
                .forEach(c -> System.out.println("  " + c.getId() + " - " + c.getFullName()));
        int id = readInt("Id du client : ");
        return Optional.ofNullable(customers.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Aucun client avec l'id " + id));
    }

    // getNightlyPrice() et getType() : le résultat dépend du vrai type de chambre (polymorphisme)
    private void printRoom(Room room) {
        System.out.printf("  Chambre %d | %-8s | %d pers. max | %.2f € / nuit%n",
                room.getRoomNumber(), room.getType(), room.getCapacity(), room.getNightlyPrice());
    }

    private void printReservation(Reservation reservation) {
        System.out.printf("  n°%d | %s | chambre %d (%s) | du %s au %s (%d nuits) | %d pers. | %s | %.2f €%n",
                reservation.getId(), reservation.getCustomer().getFullName(),
                reservation.getRoom().getRoomNumber(), reservation.getRoom().getType(),
                reservation.getCheckIn().format(DateFormats.FRENCH), reservation.getCheckOut().format(DateFormats.FRENCH), reservation.getNights(),
                reservation.getNumberOfPeople(), reservation.getStatus(),
                pricingService.computePrice(reservation));
    }

    // nextLine puis parseInt plutot que nextInt : nextInt laisse le retour a la ligne et la question suivante serait sautée
    private int readInt(String question) {
        while (true) {
            System.out.print(question);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Il faut taper un nombre entier.");
            }
        }
    }

    // On redemande tant que la date est invalide pour ne jamais faire planter l'appli sur une faute de frappe
    private LocalDate readDate(String question) {
        while (true) {
            System.out.print(question);
            try {
                return LocalDate.parse(scanner.nextLine().trim(), DateFormats.FRENCH);
            } catch (DateTimeParseException e) {
                System.out.println("Date invalide, format attendu : JJ/MM/AAAA (ex. 20/12/2026).");
            }
        }
    }
}
