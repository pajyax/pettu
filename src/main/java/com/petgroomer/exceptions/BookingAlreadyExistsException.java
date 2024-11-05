package com.petgroomer.exceptions;

public class BookingAlreadyExistsException extends RuntimeException {

    public BookingAlreadyExistsException(String message) {
        super(message);
    }
}
