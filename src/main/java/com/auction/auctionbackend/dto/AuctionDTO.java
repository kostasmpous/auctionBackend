package com.auction.auctionbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class AuctionDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double startingPrice;
    private Double buyoutPrice;
    private Double currentPrice;
    private String location;
    private String country;
    private Long sellerId;
    private Set<String> categoryNames;
}
