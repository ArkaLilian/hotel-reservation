package org.example.model;

public class StandardRoom extends Room{

    public StandardRoom(int roomNumber, int capacity, double basePrice){
        super(roomNumber, capacity, basePrice);
    }


    @Override
    public double getNightlyPrice() {
        return getBasePrice();
    }

    @Override
    public String getType() {
        return "Standard";
    }
}
