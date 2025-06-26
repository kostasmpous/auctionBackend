package com.auction.auctionbackend.dto;

import lombok.Data;

@Data
public class SellerSummaryDTO {
    private Long id;
    private String name;
    private String lastName;
    private Double rating;
}
