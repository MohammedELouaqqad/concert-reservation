package com.example.reservation_concert.service;


import com.example.reservation_concert.dto.AuthenticationResponse;
import com.example.reservation_concert.dto.RegisterRequest;
import com.example.reservation_concert.dto.UserResponse;
import com.example.reservation_concert.entity.User;
import com.example.reservation_concert.entity.UserRole;
import com.example.reservation_concert.exception.UserAlreadyExistsException;
import com.example.reservation_concert.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public UserResponse getUserConnected(){
        User userFromToken = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByEmail(userFromToken.getEmail())
              .orElseThrow(()-> new UsernameNotFoundException("User not Found"));
        return new UserResponse(
                    currentUser.getName(),
                    currentUser.getEmail(),
                    currentUser.getRole()
            );
    }

    public UserResponse createUserAdmin(RegisterRequest request){
        boolean existUser = userRepository.existsByEmail(request.getEmail());
        if(existUser){
            throw new UserAlreadyExistsException(request.getEmail());
        }
        User newUser = new User(
                request.getEmail(),
                request.getName(),
                passwordEncoder.encode(request.getPassword()),
                UserRole.ADMIN,
                request.getAge()
        );
        userRepository.save(newUser);
        return new UserResponse(newUser.getName(),newUser.getEmail(),newUser.getRole());
    }
}
