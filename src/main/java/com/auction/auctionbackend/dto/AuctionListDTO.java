package com.auction.auctionbackend.dto;

import com.auction.auctionbackend.model.Auction;
import com.auction.auctionbackend.model.AuctionStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class AuctionListDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double currentPrice;
    private SellerSummaryDTO seller;
    private Set<CategorySummaryDTO> categories;
    private Double startPrice;
    private Long bidCounts;
    private AuctionStatus status;
}
