package com.auction.auctionbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class AuctionCreateDTO {
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double startingPrice;
    private String location;
    private String country;
    private Set<Long> categoryIds;
    private Double buyoutprice;
}
