package com.example.reservation_concert.controller;


import com.example.reservation_concert.dto.RegisterRequest;
import com.example.reservation_concert.dto.UserResponse;
import com.example.reservation_concert.entity.User;
import com.example.reservation_concert.repository.UserRepository;
import com.example.reservation_concert.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserConnected(){
        return ResponseEntity.ok(userService.getUserConnected());
    }

    @PostMapping("/admin")
    public ResponseEntity<UserResponse> createUserAdmin(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.status(201).body(userService.createUserAdmin(request));
    }
}
