package org.example.model;

public class StandardRoom extends Room{

    public StandardRoom(int roomNumber, int capacity, int basePrice){
        super(roomNumber, capacity, basePrice);
    }


    @Override
    public int getNightlyPrice() {
        return getBasePrice();
    }

    @Override
    public String getType() {
        return "Standard";
    }
}
