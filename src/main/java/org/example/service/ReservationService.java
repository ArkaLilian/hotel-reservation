package org.example.service;

import org.example.exception.ReservationNotFoundException;
import org.example.exception.RoomNotAvailableException;
import org.example.model.Customer;
import org.example.model.Reservation;
import org.example.model.ReservationStatus;
import org.example.model.Room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    // Optional : la chambre peut ne pas exister, on évite de renvoyer null
    public Optional<Room> findRoom(int number) {
        return Optional.ofNullable(rooms.get(number));
    }

    // Libre = aucune réservation ACTIVE (on active un flux .stream pour filtrer notre liste de reservations)
    public boolean isAvailable(Room room, LocalDate start, LocalDate end) {
        return reservations.stream()
                .filter(Reservation::isActive)
                .filter(r -> r.getRoom().equals(room))
                .noneMatch(r -> r.overlaps(start, end));
    }

    // Chambres libres sur la période et assez grandes. Liste vide s'il n'y en a aucune (ce n'est pas une erreur ducoup)
    // On refait un stream de la liste des chambres et on vérifie room.canHost(guest) = True et isAvailable(room, start, end= True aussi)
    public List<Room> findAvailableRooms(LocalDate start, LocalDate end, int guests) {
        // Sans ces controles, 0 personne ou des dates inversées renverraient des chambres "libres"
        if (guests <= 0) {
            throw new IllegalArgumentException("Le nombre de personnes doit être positif");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("La date de départ doit être après la date d'arrivée");
        }
        return getRooms().stream()
                .filter(room -> room.canHost(guests))
                .filter(room -> isAvailable(room, start, end))
                .toList();
    }

    // Triées par numéro car une HashMap ne garde pas d'ordre et l'affichage changerait a chaque fois
    public List<Room> getRooms() {
        return rooms.values().stream()
                .sorted(Comparator.comparingInt(Room::getRoomNumber))
                .toList();
    }
    //On va creer une méthode qui réserve la chambre :
    public Reservation book(Customer customer, int roomNumber, LocalDate start, LocalDate end, int guests)
            throws RoomNotAvailableException {
        // La chambre doit exister
        Room room = findRoom(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("La chambre " + roomNumber + " n'existe pas"));

        // Il doit y avoir assez de place
        if (!room.canHost(guests)) {
            throw new IllegalArgumentException("La chambre " + roomNumber + " ne peut pas accueillir " + guests + " personnes");
        }

        // (dates/client null) : vérifiés par le constructeur de Reservation et checkOut strictement après checkIn (au moins 1 nuit)
        Reservation reservation = new Reservation(nextId, customer, room, start, end, guests, ReservationStatus.CONFIRMED);

        // Pas de réservation dans le passé
        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Impossible de réserver dans le passé");
        }

        // Pas de chevauchement avec une réservation confirmée
        if (!isAvailable(room, start, end)) {
            throw new RoomNotAvailableException(roomNumber, start, end);
        }
        reservations.add(reservation);
        nextId++; // seulement une fois la réservation réussie
        return reservation;
    }

    // Pas d'id inconnu et on vérifie si déjà annulée -> IllegalStateException levée avant par Reservation.cancel()
    public void cancel(int id) throws ReservationNotFoundException {
        Reservation reservation = reservations.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ReservationNotFoundException(id));
        reservation.cancel();
    }

    // Toutes les réservations du client (annulées comprises), de la plus ancienne arrivée à la plus récente
    public List<Reservation> findByCustomer(Customer customer) {
        return reservations.stream()
                .filter(r -> r.getCustomer().equals(customer))
                .sorted(Comparator.comparing(Reservation::getCheckIn))
                .toList();
    }

    // Lecture seule : personne ne peut modifier notre liste de l'extérieur
    public List<Reservation> getReservations() {
        return List.copyOf(reservations);
    }
}
