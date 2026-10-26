package org.example.model;

public class Suite extends Room {
    private static final double SUPPLEMENT_SUITE = 30.0;

    public Suite(int roomNumber, int capacity, double basePrice){
        super(roomNumber, capacity, basePrice);
    }

    @Override
    public double getNightlyPrice() {
        return getBasePrice() + SUPPLEMENT_SUITE;
    }

    @Override
    public String getType() {
        return "Suite";
    }
}
