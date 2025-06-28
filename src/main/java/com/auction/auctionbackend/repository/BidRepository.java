package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.Auction;
import com.auction.auctionbackend.model.Bid;
import com.auction.auctionbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionId(Long auctionId);

    boolean existsByAuction_SellerAndBidder(User seller, User bidder);

    boolean existsByAuctionId(Long auctionId);

    Optional<Bid> findTopByAuctionOrderByAmountDesc(Auction auction);
}
