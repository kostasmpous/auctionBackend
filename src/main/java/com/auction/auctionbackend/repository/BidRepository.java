package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
