package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Duration;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inTime = ticket.getInTime().getTime();
        long outTime = ticket.getOutTime().getTime();

        double HOUR_MILLIS = 1000 * 60 * 60;
        double duration = (double) (outTime - inTime) / HOUR_MILLIS;

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                if(duration < Duration.FREE)
                    ticket.setPrice(0);
                else
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                if(duration < Duration.FREE)
                    ticket.setPrice(0);
                else
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}