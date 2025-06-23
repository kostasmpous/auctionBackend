package com.auction.auctionbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;
}
