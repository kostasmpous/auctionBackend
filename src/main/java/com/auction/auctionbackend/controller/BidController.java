package com.auction.auctionbackend.controller;
import com.auction.auctionbackend.dto.BidDTO;

import com.auction.auctionbackend.dto.BidDTO;
import com.auction.auctionbackend.model.Bid;
import com.auction.auctionbackend.repository.BidRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidRepository bidRepository;

    public BidController(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    // Create bid
    @PostMapping
    public Bid createBid(@RequestBody Bid bid) {
        bid.setTime(LocalDateTime.now()); // Set current time on creation
        return bidRepository.save(bid);
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

        return dto;
    }

}
