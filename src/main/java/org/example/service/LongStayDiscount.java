package org.example.service;

import org.example.model.Reservation;

public class LongStayDiscount implements PricingRule {

    private static final double LGDISCOUNT = 0.90;
    private static final int LONG_STAY_NIGHTS = 7;


    @Override
    public double apply(Reservation reservation, double currentPrice) {
        if (reservation.getNights() >= LONG_STAY_NIGHTS) {
            return currentPrice * LGDISCOUNT;
        }
        else return currentPrice;
    }
}
