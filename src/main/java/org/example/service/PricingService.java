package org.example.service;

import org.example.model.Reservation;
import java.util.ArrayList;
import java.util.List;

public class PricingService {

    private final List<PricingRule> rules;

    public PricingService(List<PricingRule> rules) {
        if (rules == null) {
            throw new IllegalArgumentException("Les règles de prix ne peuvent pas être vides");
        }
        this.rules = new ArrayList<>(rules);
    }

    public double computePrice(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("La réservation ne peux pas être vide");
        }
        double price = reservation.getRoom().getNightlyPrice() * reservation.getNights();
        for (PricingRule rule : this.rules) {
            price = rule.apply(reservation, price);
        }
        return price;
    }
}

