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


        Message message = new Message();
        message.setContent(dto.getContent());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
        MessageDTO responseDto = new MessageDTO();
        responseDto.setContent(message.getContent());
        responseDto.setReceiverId(message.getReceiver().getId());

        responseDto.setSenderId(message.getSender().getId());
        responseDto.setTimestamp(message.getTimestamp());
        responseDto.setUnread(message.getIsUnread());
        return ResponseEntity.ok(responseDto);

    }




    @GetMapping("/received")
    public List<MessageDTO> getReceivedMessages(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findByReceiver(user).stream()
                .map(this::toDTO)
                .toList();
    }
    @GetMapping("/sent")
    public List<MessageDTO> getSentMessages(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findBySender(user).stream()
                .map(this::toDTO)
                .toList();
    }


    private MessageDTO toDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setContent(message.getContent());
        dto.setSenderId(message.getSender().getId());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setTimestamp(message.getTimestamp());
        dto.setUnread(message.getIsUnread());
        dto.setId(message.getId());

        dto.setReceiverUsername(message.getReceiver().getUsername());
        dto.setUnread(Boolean.TRUE.equals(message.getIsUnread())); // <-- safer check here
        dto.setSenderUsername(message.getSender().getUsername());
        return dto;
    }
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Principal principal) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        String currentUsername = principal.getName();
        if (!message.getReceiver().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only mark your own received messages as read.");
        }

        message.setIsUnread(false);
        messageRepository.save(message);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Integer> getUnreadMessageCount(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        int count = messageRepository.countByReceiverAndIsUnreadTrue(user);
        return ResponseEntity.ok(count);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getMessageById(@PathVariable Long id, Principal principal) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        String curUsername = principal.getName();
        if (!message.getSender().getUsername().equals(curUsername) &&
                !message.getReceiver().getUsername().equals(curUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("not authorized to view message.");
        }

        return ResponseEntity.ok(toDTO(message));
    }





}
