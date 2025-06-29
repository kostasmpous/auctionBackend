package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.dto.UserUpdateDTO;
import com.auction.auctionbackend.model.User;
import com.auction.auctionbackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // Create a new user
    @PostMapping
    public User createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // hash the password
        return userRepository.save(user);
    }



    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a single user by ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }


    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update user (basic example)
    /*@PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDetails.getUsername());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        user.setLocation(userDetails.getLocation());
        user.setTaxNumber(userDetails.getTaxNumber());
        user.setRatingAsSeller(userDetails.getRatingAsSeller());
        user.setRatingAsBidder(userDetails.getRatingAsBidder());
        //user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }
*/
    // Delete a User
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PutMapping("/username/{username}")
    public ResponseEntity<User> updateUserByUsername(@PathVariable String username, @RequestBody UserUpdateDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setLocation(dto.getLocation());
        user.setTaxNumber(dto.getTaxNumber());
        user.setRatingAsSeller(dto.getRatingAsSeller());
        user.setRatingAsBidder(dto.getRatingAsBidder());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }



    @PutMapping("/{id}/password")
    public void updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


}

