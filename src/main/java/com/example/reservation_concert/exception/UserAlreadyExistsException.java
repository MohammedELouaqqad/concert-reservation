package com.example.reservation_concert.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("This email already exist:"+email);
    }
}
