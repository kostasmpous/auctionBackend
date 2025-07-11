package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByAuctionId(Long auctionId);

}
