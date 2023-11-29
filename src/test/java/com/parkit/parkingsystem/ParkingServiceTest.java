package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest(){
        parkingService.processExitingVehicle();
        verify(ticketDAO, Mockito.times(1)).getNbTicket(anyString());
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void testProcessIncomingVehicle(){
        Mockito.reset(ticketDAO);
        when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
        parkingService.processIncomingVehicle();
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processExitingVehicleTestUnableUpdate(){
        Mockito.reset(ticketDAO, parkingSpotDAO);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void testGetNextParkingNumberIfAvailable(){
        Mockito.reset(ticketDAO, parkingSpotDAO, inputReaderUtil);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any());
        assertEquals(parkingSpot.getId(), 1);
    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound(){
        Mockito.reset(ticketDAO, parkingSpotDAO, inputReaderUtil);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(-1);
        try {
            parkingService.getNextParkingNumberIfAvailable();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "org.opentest4j.AssertionFailedError: Expected java.lang.Exception to be thrown, but nothing was thrown.");
        }
    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument(){
        Mockito.reset(ticketDAO, parkingSpotDAO, inputReaderUtil);
        when(inputReaderUtil.readSelection()).thenReturn(3);
        try {
            parkingService.getNextParkingNumberIfAvailable();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Entered input is invalid");
        }
    }
}
