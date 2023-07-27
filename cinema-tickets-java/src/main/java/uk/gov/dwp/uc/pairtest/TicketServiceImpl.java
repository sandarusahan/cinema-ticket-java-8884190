package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    private TicketPaymentService TicketPaymentService = new TicketPaymentServiceImpl();
    private SeatReservationService SeatReservationService = new SeatReservationServiceImpl();
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if (accountId == null || accountId == 0) {
            throw new InvalidPurchaseException("Account ID is invalid");
        }
        TicketValidator(ticketTypeRequests); // Validate the ticket purchase request before making the payment and reserving the seats.
        TicketPaymentService.makePayment(accountId, (int) calculateTotalCost(ticketTypeRequests)); // Make the payment.
        SeatReservationService.reserveSeat(accountId, calculateTotalSeatsToAllocate(ticketTypeRequests)); // Reserve the seats.
    }

    private double calculateTotalCost(TicketTypeRequest... ticketTypeRequests) {
        double totalCost = 0.0;
        for(TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            // Add the cost of each ticket type to the total cost.
            totalCost += getTicketPrice(ticketTypeRequest.getTicketType()) * ticketTypeRequest.getNoOfTickets();
        }
        return totalCost;
    }

    private int calculateTotalSeatsToAllocate(TicketTypeRequest... ticketTypeRequests) {
        int totalSeats = 0;
        for(TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.INFANT) {
                // Infants do not require a seat. Therefore, skip the infant ticket type.
                continue;
            }
            // Add the number of seats for each ticket type to the total seats.
            totalSeats += ticketTypeRequest.getNoOfTickets();
        }
        return totalSeats;
    }

    private double getTicketPrice(TicketTypeRequest.Type type) {
        // Ticket prices based on the table provided in the task description.
        switch (type) {
            case ADULT:
                return 20.0;
            case CHILD:
                return 10.0;
            case INFANT:
                return 0.0;
            default:
                throw new IllegalArgumentException("Unknown ticket type: " + type);
        }
    }

    private void TicketValidator(TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException{
        boolean isAdultTicketAvailable = false;
        for(TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            if (ticketTypeRequest.getNoOfTickets() < 0) {
                throw new InvalidPurchaseException("Number of tickets cannot be negative");
            }else if (ticketTypeRequest.getNoOfTickets() > 20) {
                throw new InvalidPurchaseException("Exceeded maximum number of tickets allowed per purchase (20)");
            }else if (ticketTypeRequest.getTicketType() == null) {
                throw new InvalidPurchaseException("Ticket type cannot be null");
            }
            // Check if an adult ticket is available.
            if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.ADULT && ticketTypeRequest.getNoOfTickets() > 0) {
                isAdultTicketAvailable = true;
            }
        }
        if (!isAdultTicketAvailable) {
            // If an adult ticket is not available, then throw an exception when child or infant tickets are purchased.
            for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
                if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.CHILD && ticketTypeRequest.getNoOfTickets() > 0) {
                    throw new InvalidPurchaseException("Child cannot be purchased without purchasing an Adult ticket");
                }else if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.INFANT && ticketTypeRequest.getNoOfTickets() > 0) {
                    throw new InvalidPurchaseException("Infant tickets cannot be purchased without purchasing an Adult ticket");
                }
            }
        }
    }
}
