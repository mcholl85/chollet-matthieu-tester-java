package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Duration;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    public void calculateFare(Ticket ticket, boolean discount){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inTime = ticket.getInTime().getTime();
        long outTime = ticket.getOutTime().getTime();

        double HOUR_MILLIS = 1000 * 60 * 60;
        double duration = (double) (outTime - inTime) / HOUR_MILLIS;

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(calculatePrice(duration, Fare.CAR_RATE_PER_HOUR, discount));
                break;
            }
            case BIKE: {
                ticket.setPrice(calculatePrice(duration, Fare.BIKE_RATE_PER_HOUR, discount));
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    public void calculateFare(Ticket ticket) {
        this.calculateFare(ticket, false);
    }

    private double calculatePrice(double duration, double rate, boolean discount) {
        if(duration < Duration.FREE) {
            return 0;
        }

        return discount ? rate * duration * Fare.DISCOUNT_RATE : rate * duration;
    }
}