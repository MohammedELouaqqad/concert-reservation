package com.example.reservation_concert.service;


import com.example.reservation_concert.dto.ReservationResponse;
import com.example.reservation_concert.entity.*;
import com.example.reservation_concert.exception.ConcertNotEnoughPlacesException;
import com.example.reservation_concert.exception.ConcertNotFoundException;
import com.example.reservation_concert.exception.InvalidReservationStateException;
import com.example.reservation_concert.exception.ReservationNotFoundException;
import com.example.reservation_concert.repository.ConcertRepository;
import com.example.reservation_concert.repository.ReservationRepository;
import com.example.reservation_concert.repository.UserRepository;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ReservationService {
    private final ConcertRepository concertRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;


    public ReservationService(
            ConcertRepository concertRepository,
            ReservationRepository reservationRepository,
            UserRepository userRepository
    ) {
        this.concertRepository = concertRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse createReservation(
            Long idConcert
    ){
        Concert concert=concertRepository.findById(idConcert)
                .orElseThrow(()-> new ConcertNotFoundException(idConcert));
        if(concert.getPlacesDisponibles()<1){
            throw new ConcertNotEnoughPlacesException(idConcert);
        }
        concert.setPlacesDisponibles(concert.getPlacesDisponibles()-1);
        Reservation reservation = new Reservation();
        reservation.setStatut(ReservationStatut.PENDING);
        reservation.setConcert(concert);

        User userFromToken = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userFromToken.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("User not Found"));

        reservation.setUser(currentUser);
        reservationRepository.save(reservation);
        return toReservationResponse(reservation);
    }



    public ReservationResponse toReservationResponse(Reservation reservation){
        ReservationResponse reservationResponse = new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getCreatedAt(),
                reservation.getStatut(),
                reservation.getConcert().getId()
        );
        return reservationResponse;
    }

    public List<ReservationResponse> getReservationOfUserConnected(){
        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userConnected.getRole() == UserRole.ADMIN){
            return reservationRepository.findAll()
                    .stream()
                    .map(this::toReservationResponse)
                    .collect(Collectors.toList());
        }else{
            return reservationRepository.findByUserId(userConnected.getId())
                    .stream()
                    .map(this::toReservationResponse)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public ReservationResponse confirmReservation(Long id) throws AuthenticationException {
        Reservation reservation=reservationRepository.findById(id)
                .orElseThrow(()-> new ReservationNotFoundException(id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getId().equals(reservation.getUser().getId())){
            throw new AuthorizationDeniedException("Cette Reservation pas pour vous sinon Ressayer!");
        }
        if(reservation.getStatut()!=ReservationStatut.PENDING){
            throw new InvalidReservationStateException(
                    "confirmation impossible : statut = " + reservation.getStatut()
            );
        }
        reservation.setStatut(ReservationStatut.CONFIRMED);
        return toReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id){
        Reservation reservation=reservationRepository.findById(id)
                .orElseThrow(()-> new ReservationNotFoundException(id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getId().equals(reservation.getUser().getId())){
            throw new AuthorizationDeniedException("Cette Reservation pas pour vous sinon Ressayer!");
        }
        if(reservation.getStatut()==ReservationStatut.CANCELLED){
            throw new InvalidReservationStateException("déjà annulée");
        }
        reservation.getConcert().setPlacesDisponibles(reservation.getConcert().getPlacesDisponibles()+1);
        reservation.setStatut(ReservationStatut.CANCELLED);
        return toReservationResponse(reservation);
    }
}
