package org.example.model;

public class Suite extends Room {
    private static final int SUPPLEMENT_SUITE = 30;

    public Suite(int roomNumber, int capacity, int basePrice){
        super(roomNumber, capacity, basePrice);
    }

    @Override
    public int getNightlyPrice() {
        return getBasePrice() + SUPPLEMENT_SUITE;
    }

    @Override
    public String getType() {
        return "Suite";
    }
}
