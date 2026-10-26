package org.example.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {
    private final int id;
    private final Customer customer;
    private final Room room;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int numberOfPeople;
    private ReservationStatus status; //La classe enum qu'on a créer qui sert uniquement a definir Cancelled ou Confirmed utile si on veut rajouter des status plus tard

    public Reservation(int id, Customer customer, Room room, LocalDate checkIn, LocalDate checkOut, int numberOfPeople,ReservationStatus status) {
        if (id <= 0) {
            throw new IllegalArgumentException("ReservationId doit être supérieur à 0");
        }
        this.id = id;

        if (customer == null) {
            throw new IllegalArgumentException("Client ne peut pas être vide");
        }
        this.customer = customer;

        if (room == null) {
            throw new IllegalArgumentException("La chambre ne peut pas être null");
        }
        this.room = room;

        if (checkIn == null) {
            throw new IllegalArgumentException("Le jour de CheckIn ne peut pas être vide");
        }
        this.checkIn = checkIn;

        if (checkOut == null) {
            throw new IllegalArgumentException("Le jour de CheckOut ne peut pas être vide");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Le CheckOut doit être après le CheckIn");
        }
        this.checkOut = checkOut;

        if (numberOfPeople <= 0) {
            throw new IllegalArgumentException("Le nombre de personne doit être positif !");
        }
        this.numberOfPeople = numberOfPeople;

        if (status == null) {
            throw new IllegalArgumentException("Le status ne peut être vide");
        }
        this.status = status;
    }

    public int getNights() {return (int) ChronoUnit.DAYS.between(checkIn, checkOut);}    //Normalement avec un long mais je préfère int
    public int getId() {return id;}
    public Customer getCustomer() {return customer;}
    public Room getRoom() {return room;}
    public LocalDate getCheckIn() {return checkIn;}
    public LocalDate getCheckOut() {return checkOut;}
    public int getNumberOfPeople() {return numberOfPeople;}
    public ReservationStatus getStatus() {return status;}

    public boolean isActive() {
        return status == ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        if (status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("La réservation est déjà annulée");
        }
        status = ReservationStatus.CANCELLED;
    }

    // true = les dates demandées (start → end) chevauchent cette réservation (checkIn → checkOut)
    public boolean overlaps(LocalDate start, LocalDate end) {
        // Chevauchement si l'autre période commence avant mon départ ET finit après mon arrivée.
        // Comparaisons strictes : partir le 24 et arriver le 24 n'est pas un conflit.
        // On a deja verifié que Checkin soit avant Cehckout on doit juste verifier que la prochaine reservation (start et end)
        // Start après checkout OU end avant checkin marche (ATTENTION OVERLAPS -> True veut dire chevauchement)
        return start.isBefore(checkOut) && end.isAfter(checkIn);
    }




}
