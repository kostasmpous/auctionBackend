package com.auction.auctionbackend.dto;

import lombok.Data;

@Data
public class BidRequestDTO {
    private Double amount;
    private Long auctionId;
    private Long bidderId;
}
