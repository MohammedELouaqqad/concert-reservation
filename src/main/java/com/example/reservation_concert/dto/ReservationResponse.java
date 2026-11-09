package com.example.reservation_concert.dto;

import com.example.reservation_concert.entity.ReservationStatut;
import com.example.reservation_concert.entity.User;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {

    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private ReservationStatut statut;
    private Long concertId;
}
