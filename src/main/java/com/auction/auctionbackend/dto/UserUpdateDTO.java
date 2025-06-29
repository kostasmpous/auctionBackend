package com.auction.auctionbackend.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String location;
    private String taxNumber;
    private Double ratingAsSeller;
    private Double ratingAsBidder;
    // NO password here
}
