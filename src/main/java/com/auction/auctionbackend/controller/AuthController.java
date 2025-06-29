package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.dto.JwtResponseDTO;
import com.auction.auctionbackend.dto.LoginRequestDTO;
import com.auction.auctionbackend.model.User;
import com.auction.auctionbackend.repository.UserRepository;
import com.auction.auctionbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    /*Use this to crosscheck username and password and we generate the JWT
     for the user and return it to be used to later APIs*/
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Use secure hash check
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = JwtUtil.generateToken(user.getUsername());
                return ResponseEntity.ok(new JwtResponseDTO(token));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

}
