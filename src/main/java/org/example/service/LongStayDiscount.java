package org.example.service;

import org.example.model.Reservation;

public class LongStayDiscount implements PricingRule {

    private static final double LGDISCOUNT_PERCENT = -10; //Pourcentage de réduction (attention!!! type double sinon division entière)
    private static final int LONG_STAY_NIGHTS = 7; // Constante pour la quelle on décide que le voyage est long donc mérite la réduc.


    @Override
    public double apply(Reservation reservation, double currentPrice) {
        if (reservation.getNights() >= LONG_STAY_NIGHTS) {
            return currentPrice * (1+(LGDISCOUNT_PERCENT/100));
        }
        else return currentPrice;
    }
}
