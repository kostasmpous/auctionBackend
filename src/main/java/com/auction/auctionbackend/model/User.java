package com.auction.auctionbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;
    private String phone;
    private String address;
    private String location;
    private String taxNumber; // AFM

    @Column(name = "rating_as_seller")
    private Double ratingAsSeller;

    @Column(name = "rating_as_bidder")
    private Double ratingAsBidder;
}
