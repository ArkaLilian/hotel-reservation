package org.example.utils;

import org.example.model.Customer;
import org.example.model.Reservation;
import org.example.model.ReservationStatus;
import org.example.model.Room;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvLoader {
    private static final String SEPARATOR = ";";

    // Les lignes ignorées sont gardées ici : c'est Main qui les affiche, le loader ne fait pas de System.out
    private final List<String> warnings = new ArrayList<>();

    // IOException pas attrapée ici : sans le fichier on ne peut rien faire, ducoup on la laisse remonter jusqu'a Main
    public Map<Integer, Room> loadRooms(Path path) throws IOException {
        Map<Integer, Room> rooms = new HashMap<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (int i = 1; i < lines.size(); i++) { // i = 1 pour sauter l'entête
            if (lines.get(i).isBlank()) {
                continue;
            }
            // Une ligne cassée est ignorée et signalée, les autres sont quand même chargées
            // (NumberFormatException hérite de IllegalArgumentException donc elle est attrapée aussi)
            try {
                String[] fields = split(lines.get(i), 4);
                Room room = RoomFactory.create(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Double.parseDouble(fields[3]));
                if (rooms.containsKey(room.getRoomNumber())) {
                    throw new IllegalArgumentException("la chambre " + room.getRoomNumber() + " existe deja");
                }
                rooms.put(room.getRoomNumber(), room);
            } catch (IllegalArgumentException e) {
                addWarning(path, i, e);
            }
        }
        return rooms;
    }

    public Map<Integer, Customer> loadCustomers(Path path) throws IOException {
        Map<Integer, Customer> customers = new HashMap<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue;
            }
            try {
                String[] fields = split(lines.get(i), 4);
                Customer customer = new Customer(Integer.parseInt(fields[0]), fields[1], fields[2], fields[3]);
                if (customers.containsKey(customer.getId())) {
                    throw new IllegalArgumentException("le client " + customer.getId() + " existe deja");
                }
                customers.put(customer.getId(), customer);
            } catch (IllegalArgumentException e) {
                addWarning(path, i, e);
            }
        }
        return customers;
    }

    // On reçoit les chambres et les clients deja chargés car une réservation ne contient que leurs id
    public List<Reservation> loadReservations(Path path, Map<Integer, Room> rooms, Map<Integer, Customer> customers) throws IOException {
        List<Reservation> reservations = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue;
            }
            try {
                String[] fields = split(lines.get(i), 7);
                Customer customer = customers.get(Integer.parseInt(fields[1]));
                if (customer == null) {
                    throw new IllegalArgumentException("client " + fields[1] + " inconnu");
                }
                Room room = rooms.get(Integer.parseInt(fields[2]));
                if (room == null) {
                    throw new IllegalArgumentException("chambre " + fields[2] + " inconnue");
                }
                reservations.add(new Reservation(Integer.parseInt(fields[0]), customer, room,
                        LocalDate.parse(fields[3]), LocalDate.parse(fields[4]),
                        Integer.parseInt(fields[5]), ReservationStatus.valueOf(fields[6])));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                addWarning(path, i, e);
            }
        }
        return reservations;
    }

    public List<String> getWarnings() {
        return List.copyOf(warnings);
    }

    // Sans ce controle une ligne avec une colonne en moins planterait avec un ArrayIndexOutOfBoundsException
    private String[] split(String line, int expectedColumns) {
        String[] fields = line.split(SEPARATOR);
        if (fields.length != expectedColumns) {
            throw new IllegalArgumentException(expectedColumns + " colonnes attendues, " + fields.length + " trouvées");
        }
        for (int j = 0; j < fields.length; j++) {
            fields[j] = fields[j].trim();
        }
        return fields;
    }

    // i + 1 car la liste commence a 0 alors que dans le fichier la première ligne est la ligne 1
    private void addWarning(Path path, int i, RuntimeException e) {
        warnings.add(path.getFileName() + " ligne " + (i + 1) + " ignorée : " + e.getMessage());
    }
}
