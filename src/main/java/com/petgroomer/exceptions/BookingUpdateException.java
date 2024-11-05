package com.petgroomer.exceptions;

public class BookingUpdateException extends RuntimeException {

    public BookingUpdateException(Long id, String message) {
        super("Failed to update booking with id: " + id + ". " + message);
    }
}
