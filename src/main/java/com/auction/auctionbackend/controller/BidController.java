package com.auction.auctionbackend.controller;
import com.auction.auctionbackend.dto.BidDTO;

import com.auction.auctionbackend.dto.BidRequestDTO;
import com.auction.auctionbackend.model.Auction;
import com.auction.auctionbackend.model.Bid;
import com.auction.auctionbackend.model.User;
import com.auction.auctionbackend.repository.AuctionRepository;
import com.auction.auctionbackend.repository.BidRepository;
import com.auction.auctionbackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidRepository bidRepository;
    // Add dependencies to the constructor
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    public BidController(BidRepository bidRepository, AuctionRepository auctionRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
    }


    // Create bid
    @PostMapping
    public ResponseEntity<BidDTO> createBid(@RequestBody BidRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        User bidder = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Bid bid = new Bid();
        bid.setAmount(request.getAmount());
        bid.setAuction(auction);
        bid.setBidder(bidder);
        bid.setTime(LocalDateTime.now());

        Bid saved = bidRepository.save(bid);

        // Convert to DTO
        BidDTO dto = new BidDTO();
        dto.setId(saved.getId());
        dto.setAmount(saved.getAmount());
        dto.setTime(saved.getTime());
        dto.setAuctionId(saved.getAuction().getId());
        dto.setBidderId(saved.getBidder().getId());
        dto.setBidderUsername(saved.getBidder().getUsername());

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public List<BidDTO> getAllBids() {
        return bidRepository.findAll().stream()
                .map(bid -> {
                    BidDTO dto = new BidDTO();
                    dto.setId(bid.getId());
                    dto.setAmount(bid.getAmount());
                    dto.setTime(bid.getTime());
                    dto.setAuctionId(bid.getAuction().getId());
                    dto.setBidderId(bid.getBidder().getId());
                    dto.setBidderUsername(bid.getBidder().getUsername());
                    return dto;
                })
                .toList();
    }


    // Get bid by ID
    @GetMapping("/{id}")
    public BidDTO getBidById(@PathVariable Long id) {
        Bid bid = bidRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        BidDTO dto = new BidDTO();
        dto.setId(bid.getId());
        dto.setAmount(bid.getAmount());
        dto.setTime(bid.getTime());
        dto.setAuctionId(bid.getAuction().getId());
        dto.setBidderId(bid.getBidder().getId());
        dto.setBidderUsername(bid.getBidder().getUsername());
        dto.setBidderLocation(bid.getBidder().getLocation());
        dto.setBidderRating(bid.getBidder().getRatingAsBidder());
        return dto;
    }

}
