package com.example.reservation_concert.controller;

import com.example.reservation_concert.entity.ErrorResponse;
import com.example.reservation_concert.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


//GlobalExceptionHandler (@RestControllerAdvice) : centralise la gestion des exceptions pour éviter
// que le client reçoive des erreurs vagues (500 + stack trace). Le service lève l’exception (throw),
// le controller ne la gère pas (il la laisse remonter), et le @RestControllerAdvice l’intercepte
// automatiquement au passage → la transforme en réponse HTTP propre (code + message JSON lisible).
// Sans ce handler, Spring ne sait pas quoi faire de l’exception → 500 par défaut. Avec, chaque type
// d’exception est traduit en bon code HTTP (404, 409, 400)

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConcertNotFoundException.class)
    public ResponseEntity<?> handleConcertNotFoundException(ConcertNotFoundException exception){
        ErrorResponse concertNotFound = new ErrorResponse(LocalDateTime.now(),exception.getMessage(),"Concert Not Found");
        return new ResponseEntity<>(concertNotFound, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<?> handleReservationNotFoundException(ReservationNotFoundException exception){
        ErrorResponse reservationNotFound = new ErrorResponse(LocalDateTime.now(),exception.getMessage(),"Reservation Not Found");
        return new ResponseEntity<>(reservationNotFound, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConcertNotEnoughPlacesException.class)
    public ResponseEntity<?> handleConcertNotEnoughPlacesException(ConcertNotEnoughPlacesException exception){
        ErrorResponse concertNotEnoughPlaces = new ErrorResponse(LocalDateTime.now(),exception.getMessage(),"Pas de place dans ce Concert");
        return new ResponseEntity<>(concertNotEnoughPlaces, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConcertModificationException.class)
    public ResponseEntity<?> handleConcertModificationException(ConcertModificationException exception){
        ErrorResponse concertModification = new ErrorResponse(LocalDateTime.now(),exception.getMessage(),"Pas de modification de ce Concert");
        return new ResponseEntity<>(concertModification, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ReservationConfirmedException.class)
    public ResponseEntity<?> handleReservationConfirmedException(ReservationConfirmedException exception){
        ErrorResponse reservationConfirmed = new ErrorResponse(LocalDateTime.now(),exception.getMessage(),"Error dans confimation de cette Reservation");
        return new ResponseEntity<>(reservationConfirmed, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidReservationStateException.class)
    public ResponseEntity<?> handleInvalidReservationStateException(InvalidReservationStateException exception){
        ErrorResponse invalidReservationState = new ErrorResponse(LocalDateTime.now(),exception.getMessage(),"Reservation Invalid");
        return new ResponseEntity<>(invalidReservationState, HttpStatus.CONFLICT);
    }

    //attention pas bien pratiquer de mets exception.getMessage()

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        ErrorResponse methodArgumentNotValid = new ErrorResponse(LocalDateTime.now(),exception.getMessage(),"Les donnees saisis pas valid, Ressayer");
        return new ResponseEntity<>(methodArgumentNotValid, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException exception){
        ErrorResponse userAlreadyExistsException = new ErrorResponse(LocalDateTime.now(),exception.getMessage(),"User deja exists");
        return new ResponseEntity<>(userAlreadyExistsException, HttpStatus.CONFLICT);
    }



}
