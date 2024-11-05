package com.petgroomer.exceptions;

public class BookingCancellationException extends RuntimeException {

    public BookingCancellationException(Long id) {
        super("Failed to cancel booking with id: " + id);
    }

    public BookingCancellationException(Long id, String message) {
        super("Failed to cancel booking with id: " + id + ". " + message);
    }
}
