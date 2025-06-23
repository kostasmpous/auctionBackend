package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
