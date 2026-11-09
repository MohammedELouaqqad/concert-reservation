package com.example.reservation_concert.dto;


import com.example.reservation_concert.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private String name;
    private String email;
    private UserRole role;
}
