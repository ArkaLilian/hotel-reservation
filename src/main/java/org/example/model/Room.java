package org.example.model;

import java.util.Objects;

public abstract class Room {
    private final int roomNumber;
    private final int capacity;
    private final double basePrice;

    protected Room(int roomNumber, int capacity, double basePrice) {
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Le numéro de chambre doit être strictement positif");
        }
        this.roomNumber = roomNumber;

        if (capacity <= 0) {
            throw new IllegalArgumentException("La capacité de la chambre doit être strictement positive");
        }
        this.capacity = capacity;

        if  (basePrice <= 0) {
            throw new IllegalArgumentException("Le prix de base doit être strictement positif");
        }
        this.basePrice = basePrice;
    }
    //Méthodes abstraites :
    public abstract double getNightlyPrice();
    public abstract String getType();

    //Méthodes concrètes :
    public int getRoomNumber() {
        return roomNumber;
    }
    public int getCapacity() {return capacity;}
    public double getBasePrice() {return basePrice;}

    public boolean canHost(int guests) {return guests <= capacity;}


    //Comme vu dans le cours je vais utiliser le "contrat" entre hashCode et equals pour le numero de chambre.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Room other)) {
            return false;
        }
        return this.roomNumber == other.roomNumber;
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(roomNumber);
    }
}