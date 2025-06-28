package com.auction.auctionbackend.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private Long id;
    private String content;
    private Long receiverId;
    private Long senderId;
    private Boolean unread;
    private java.time.LocalDateTime timestamp;
}


