package com.example.reservation_concert.exception;

public class ConcertNotFoundException extends RuntimeException {
    public ConcertNotFoundException(Long id) {
        super("Concert introuvable : " + id);
    }
}
