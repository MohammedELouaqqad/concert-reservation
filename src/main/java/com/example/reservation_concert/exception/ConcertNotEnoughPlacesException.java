package com.example.reservation_concert.exception;

public class ConcertNotEnoughPlacesException extends RuntimeException {
    public ConcertNotEnoughPlacesException(Long id) {
        super("Nombre de place n'est pas suffisant, Concert : " + id);
    }
}
