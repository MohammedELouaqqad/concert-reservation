package com.example.reservation_concert.controller;


import com.example.reservation_concert.dto.CreateConcertDTO;
import com.example.reservation_concert.dto.ConcertResponse;
import com.example.reservation_concert.service.ConcertService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/concerts")
public class ConcertController {

    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @PostMapping
    public ResponseEntity<ConcertResponse> createConcert(@Valid @RequestBody CreateConcertDTO newConcert){
        return ResponseEntity.status(201).body(concertService.createConcert(newConcert));
    }

    @GetMapping
    public ResponseEntity<List<ConcertResponse>> getAllConcerts(){
        return ResponseEntity.ok(concertService.getAllConcerts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConcertResponse> getConcertById(@PathVariable Long id){
        return ResponseEntity.ok(concertService.getConcertById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ConcertResponse> modifyConcert(@PathVariable Long id,@Valid @RequestBody CreateConcertDTO concert){
        return ResponseEntity.ok(concertService.modifyConcert(id, concert.getName(), concert.getDate(), concert.getPlacesDisponibles()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcert(@PathVariable Long id){
        concertService.deleteConcert(id);
        return ResponseEntity.noContent().build();
    }

}
