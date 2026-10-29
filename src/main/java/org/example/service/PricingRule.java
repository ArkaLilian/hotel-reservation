package org.example.service;

import org.example.model.Reservation;

public interface PricingRule {
    double apply(Reservation reservation, double currentPrice);
}
