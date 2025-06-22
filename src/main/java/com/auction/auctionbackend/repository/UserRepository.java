package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
