package com.example.reservation_concert.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConcertResponse {

    private Long id;
    private String name;
    private LocalDateTime date;
    private int placesDisponibles;
}
