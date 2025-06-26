package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    @Query("SELECT DISTINCT a FROM Auction a " +
            "LEFT JOIN FETCH a.categories " +
            "LEFT JOIN FETCH a.seller")
    List<Auction> findAllWithCategoriesAndSeller();

    @Query("SELECT a FROM Auction a " +
            "LEFT JOIN FETCH a.categories " +
            "LEFT JOIN FETCH a.seller " +
            "WHERE a.id = :id")
    Optional<Auction> findByIdWithCategoriesAndSeller(@Param("id") Long id);

}
