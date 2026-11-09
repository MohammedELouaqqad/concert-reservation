package com.example.reservation_concert.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String details;


    public ErrorResponse(LocalDateTime now, String message, String details) {
        this.timestamp=now;
        this.details=details;
        this.message=message;
    }
}
