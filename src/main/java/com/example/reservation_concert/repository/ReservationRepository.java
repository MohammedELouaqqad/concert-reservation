package com.example.reservation_concert.repository;

import com.example.reservation_concert.entity.Concert;
import com.example.reservation_concert.entity.Reservation;
import com.example.reservation_concert.entity.ReservationStatut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    boolean existsByConcertAndStatut(Concert concert, ReservationStatut statut);

    List<Reservation> findByUserId(Long id);
}
