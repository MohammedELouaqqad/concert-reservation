package com.example.reservation_concert.repository;

import com.example.reservation_concert.entity.Concert;
import com.example.reservation_concert.dto.ConcertResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcertRepository extends JpaRepository<Concert,Long> {
    Optional<Concert> findConcertById(Long id);

    void deleteConcertById(Long id);


    // @EntityGraph sur findAll() = inutile ici : se comporterait comme EAGER
    // (charge toutes les réservations de tous les concerts, même pour un simple listing).
//    @EntityGraph(attributePaths = "reservationList")
//    List<Concert> findAll();
//
    // Mieux : garder findAll() tel quel -> concerts seuls, reservationList en proxy lazy (non chargée).
    // Et définir une méthode dédiée au besoin "concert + ses réservations" :
    // findWithReservationsById(Long id) avec @EntityGraph -> 1 concert, reservationList
    // complète, en une seule requête (JOIN).
}
