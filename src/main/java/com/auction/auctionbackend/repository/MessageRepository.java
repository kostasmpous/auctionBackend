package com.auction.auctionbackend.repository;

import com.auction.auctionbackend.model.Message;
import com.auction.auctionbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrReceiver(User sender, User receiver);
}
