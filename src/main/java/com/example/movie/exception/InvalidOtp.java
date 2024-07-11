package com.example.movie.exception;

public class InvalidOtp extends RuntimeException {

    public InvalidOtp(String message) {
        super(message);
    }
}
