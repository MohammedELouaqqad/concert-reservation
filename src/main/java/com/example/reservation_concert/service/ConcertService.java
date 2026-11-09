package com.example.reservation_concert.service;


import com.example.reservation_concert.dto.CreateConcertDTO;
import com.example.reservation_concert.entity.Concert;
import com.example.reservation_concert.dto.ConcertResponse;
import com.example.reservation_concert.entity.ReservationStatut;
import com.example.reservation_concert.exception.ConcertModificationException;
import com.example.reservation_concert.exception.ConcertNotFoundException;
import com.example.reservation_concert.exception.ReservationConfirmedException;
import com.example.reservation_concert.repository.ConcertRepository;

import com.example.reservation_concert.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ReservationRepository reservationRepository;

    public ConcertService(ConcertRepository concertRepository, ReservationRepository reservationRepository) {
        this.concertRepository = concertRepository;
        this.reservationRepository = reservationRepository;
    }

    public ConcertResponse createConcert(CreateConcertDTO request){
        Concert concert=new Concert(request.getName(),request.getDate(),request.getPlacesDisponibles());
        concertRepository.save(concert);
        return toConcertResponse(concert);
    }
    private ConcertResponse toConcertResponse(Concert concert){
        ConcertResponse concertResponse = new ConcertResponse(
                concert.getId(),
                concert.getName(),
                concert.getDate(),
                concert.getPlacesDisponibles()
        );
        return concertResponse;
    }

    public List<ConcertResponse> getAllConcerts(){

        return concertRepository.findAll()
                .stream()
                .map(this::toConcertResponse)
                .collect(Collectors.toList());
    }

    public ConcertResponse getConcertById(Long id){
        Concert concert=concertRepository.findById(id)
                .orElseThrow(() -> new ConcertNotFoundException(id));
        return toConcertResponse(concert);
    }


//    sans @Transactional → session fermée au moment de l’accès
//                        → LazyInitializationException
//    avec @Transactional → session ouverte pendant toute la méthode
//                        → l’accès déclenche le SELECT normalement
    @Transactional
    public ConcertResponse modifyConcert(Long id, String nom, LocalDateTime date, int places){
        Concert currentConcert=concertRepository.findById(id)
                .orElseThrow(() -> new ConcertNotFoundException(id));
        if(currentConcert.getReservationList().isEmpty()){
            currentConcert.setName(nom);
            currentConcert.setDate(date);
            currentConcert.setPlacesDisponibles(places);
            return toConcertResponse(currentConcert);
        }else{
            throw new ConcertModificationException("impossible : réservations existantes");
        }
    }

    @Transactional
    public void deleteConcert(Long id) {
        Concert concert=concertRepository.findById(id)
                .orElseThrow(() -> new ConcertNotFoundException(id));
        boolean reservationCONFIRMED = reservationRepository.existsByConcertAndStatut(concert,ReservationStatut.CONFIRMED);
        if(reservationCONFIRMED){
            throw new ReservationConfirmedException("impossible : réservations confirmées");
        }else{
            concertRepository.deleteById(id);
        }
    }
}
