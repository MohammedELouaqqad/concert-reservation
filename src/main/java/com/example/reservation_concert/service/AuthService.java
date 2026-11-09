package com.example.reservation_concert.service;


import com.example.reservation_concert.config.JwtService;
import com.example.reservation_concert.dto.AuthenticationRequest;
import com.example.reservation_concert.dto.AuthenticationResponse;
import com.example.reservation_concert.dto.RegisterRequest;
import com.example.reservation_concert.entity.User;
import com.example.reservation_concert.entity.UserRole;
import com.example.reservation_concert.exception.UserAlreadyExistsException;
import com.example.reservation_concert.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }



    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("Email Not Found"));

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .token(jwtToken)
                .build();
    }
    public AuthenticationResponse register(RegisterRequest request){
        boolean existUser = userRepository.existsByEmail(request.getEmail());
        if(existUser){
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User newUser = new User(
                request.getEmail(),
                request.getName(),
                passwordEncoder.encode(request.getPassword()),
                UserRole.USER,
                request.getAge()
        );

        userRepository.save(newUser);
        String jwtToken = jwtService.generateToken(newUser);
        return AuthenticationResponse.builder()
                .name(newUser.getName())
                .role(newUser.getRole())
                .email(newUser.getEmail())
                .token(jwtToken)
                .build();
    }
}
