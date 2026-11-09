package com.example.reservation_concert.controller;


import com.example.reservation_concert.dto.ReservationResponse;
import com.example.reservation_concert.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/concerts/{idConcert}/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @PathVariable Long idConcert){
        return ResponseEntity.status(201).body(reservationService.createReservation(idConcert));
    }

    @PutMapping("/reservations/{id}/confirm")
    public ResponseEntity<ReservationResponse> confirmReservation(@PathVariable Long id){
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    @PutMapping("/reservations/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id){
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations(){
        return ResponseEntity.ok(reservationService.getReservationOfUserConnected());
    }
}
