package com.example.reservation_concert.dto;


import com.example.reservation_concert.entity.UserRole;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private int age;
}
