package com.example.reservation_concert.dto;


import com.example.reservation_concert.entity.User;
import com.example.reservation_concert.entity.UserRole;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String name;
    private String email;
    private UserRole role;
    private String token;
}
