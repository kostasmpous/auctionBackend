package com.auction.auctionbackend.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private String content;
    private Long receiverId;
}

