package com.auction.auctionbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BidDTO {
    private Long id;
    private Double amount;
    private LocalDateTime time;
    private Long auctionId;
    private Long bidderId;
    private String bidderUsername;
}
