package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
