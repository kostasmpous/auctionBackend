package com.auction.auctionbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuctionMinimalDTO {
    private Long id;
    private String name;
    private String description;
    private Double startingPrice;
    private Double currentPrice;
    private Long bidCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
