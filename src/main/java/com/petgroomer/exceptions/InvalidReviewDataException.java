package com.petgroomer.exceptions;

public class InvalidReviewDataException extends RuntimeException {
    public InvalidReviewDataException(String message) {
        super(message);
    }
}