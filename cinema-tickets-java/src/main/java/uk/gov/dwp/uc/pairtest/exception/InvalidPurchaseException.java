package uk.gov.dwp.uc.pairtest.exception;

public class InvalidPurchaseException extends RuntimeException {

    public InvalidPurchaseException() {
        super("Invalid purchase");
    }
    public InvalidPurchaseException(String message) {
        super(message);
    }
}
