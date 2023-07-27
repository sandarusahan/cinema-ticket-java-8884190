import org.junit.Test;

import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceTest {
   
    TicketService ticketService = new TicketServiceImpl();

    // Expected: InvalidPurchaseException when Account ID is null
    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaceTicket1() {
        ticketService.purchaseTickets(null);
    }

    // Expected: InvalidPurchaseException when Account ID is 0
    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaceTicket2() {
        ticketService.purchaseTickets((long) 0);
    }

    // Expected: InvalidPurchaseException when Number of tickets exceeds 20
    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaceTicket3() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 21);
        ticketService.purchaseTickets((long) 1234, ticketTypeRequest);
    }
    
    // Expected: InvalidPurchaseException when Number of tickets is negative
    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaceTicket4() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1);
        ticketService.purchaseTickets((long) 1234, ticketTypeRequest);
    }

    // Expected: InvalidPurchaseException when Ticket type is null
    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaceTicket5() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(null, 1);
        ticketService.purchaseTickets((long) 1234, ticketTypeRequest);
    }

    // Expected: InvalidPurchaseException when child ticket is purchased without an adult ticket
    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaceTicket6() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        ticketService.purchaseTickets((long) 1234, ticketTypeRequest1, ticketTypeRequest2);
    }

    // Expected: InvalidPurchaseException when infant ticket is purchased without an adult ticket
    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaceTicket7() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        ticketService.purchaseTickets((long) 1234, ticketTypeRequest1);
    }

    // Calculations of total cost and number seats cannot be tested as they are private methods. 
    // Therefore, the following test does not include any assertions.
    @Test
    public void testPurchaceTicket8() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);
        ticketService.purchaseTickets((long) 1234, ticketTypeRequest1, ticketTypeRequest2, ticketTypeRequest3);
    }
}
