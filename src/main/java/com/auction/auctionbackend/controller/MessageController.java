package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.dto.MessageDTO;
import com.auction.auctionbackend.model.Message;
import com.auction.auctionbackend.model.User;
import com.auction.auctionbackend.repository.BidRepository;
import com.auction.auctionbackend.repository.MessageRepository;
import com.auction.auctionbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;


    public MessageController(MessageRepository messageRepository, UserRepository userRepository, BidRepository bidRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO dto, Principal principal) {
        String senderUsername = principal.getName();
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        boolean hasBid = bidRepository.existsByAuction_SellerAndBidder(receiver, sender);

        if (!hasBid) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only message sellers you have placed a bid with.");
        }

        Message message = new Message();
        message.setContent(dto.getContent());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
        return ResponseEntity.ok(message);
    }



    @GetMapping("/user/{userId}")
    public List<Message> getUserMessages(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findBySenderOrReceiver(user, user);
    }
}
