package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.Bid;
import com.auction.auctionbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
    boolean existsByAuction_SellerAndBidder(User seller, User bidder);
    java.util.List<Bid> findByAuctionId(Long auctionId);
    boolean existsByAuctionId(Long auctionId);


}
