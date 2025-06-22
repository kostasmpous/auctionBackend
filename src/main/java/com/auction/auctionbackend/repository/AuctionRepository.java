package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    @Query("SELECT DISTINCT a FROM Auction a " +
            "LEFT JOIN FETCH a.categories " +
            "LEFT JOIN FETCH a.seller")
    List<Auction> findAllWithCategoriesAndSeller();

}
