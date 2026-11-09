package com.example.reservation_concert.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
@Setter
public class CreateConcertDTO {
    @NotBlank
    private String name;
    private LocalDateTime date;
    private int placesDisponibles;


    public CreateConcertDTO(String name, LocalDateTime date, int placesDisponibles) {
        this.name=name;
        this.date=date;
        this.placesDisponibles=placesDisponibles;
    }
}
