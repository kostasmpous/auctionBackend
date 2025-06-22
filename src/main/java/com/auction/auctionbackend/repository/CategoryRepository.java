package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
