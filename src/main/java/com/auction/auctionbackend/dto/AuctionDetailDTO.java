package com.auction.auctionbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class AuctionDetailDTO {
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
    private SellerSummaryDTO seller;
    private Set<CategorySummaryDTO> categories;
    private List<BidDTO> bids;
    private int bidCount;
    private List<String> photoUrls;

}