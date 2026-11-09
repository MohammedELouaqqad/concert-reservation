package com.example.reservation_concert.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(Long id) {
        super("Reservation introuvable : " + id);
    }
}
