package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.dto.*;
import com.auction.auctionbackend.model.Auction;
import com.auction.auctionbackend.repository.AuctionRepository;
import com.auction.auctionbackend.repository.BidRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    public AuctionController(AuctionRepository auctionRepository, BidRepository bidRepository ) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
    }

    @PostMapping
    public Auction createAuction(@RequestBody Auction auction) {
        return auctionRepository.save(auction);
    }



    @GetMapping
    public List<AuctionListDTO> getAllAuctions() {
        return auctionRepository.findAllWithCategoriesAndSeller().stream()
                .map(auction -> {
                    AuctionListDTO dto = new AuctionListDTO();
                    dto.setId(auction.getId());
                    dto.setName(auction.getName());
                    dto.setDescription(auction.getDescription());
                    dto.setStartTime(auction.getStartTime());
                    dto.setEndTime(auction.getEndTime());
                    dto.setCurrentPrice(auction.getCurrentPrice());
                    dto.setBidCounts(auction.getBidCount());
                    dto.setStartPrice(auction.getStartingPrice());
                    // Map seller
                    SellerSummaryDTO sellerDto = new SellerSummaryDTO();
                    sellerDto.setId(auction.getSeller().getId());
                    sellerDto.setName(auction.getSeller().getFirstName());
                    sellerDto.setLastName(auction.getSeller().getLastName());
                    dto.setSeller(sellerDto);

                    // Map categories
                    Set<CategorySummaryDTO> categoryDtos = auction.getCategories().stream()
                            .map(cat -> {
                                CategorySummaryDTO cDto = new CategorySummaryDTO();
                                cDto.setId(cat.getId());
                                cDto.setName(cat.getName());
                                return cDto;
                            }).collect(Collectors.toSet());

                    dto.setCategories(categoryDtos);

                    return dto;
                })
                .toList();
    }


    @GetMapping("/{id}")
    public AuctionDetailDTO getAuctionById(@PathVariable Long id) {
        Auction auction = auctionRepository.findByIdWithCategoriesAndSeller(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        AuctionDetailDTO dto = new AuctionDetailDTO();
        dto.setId(auction.getId());
        dto.setName(auction.getName());
        dto.setDescription(auction.getDescription());
        dto.setStartTime(auction.getStartTime());
        dto.setEndTime(auction.getEndTime());
        dto.setStartingPrice(auction.getStartingPrice());
        dto.setBuyoutPrice(auction.getBuyoutPrice());
        dto.setCurrentPrice(auction.getCurrentPrice());
        dto.setLocation(auction.getLocation());
        dto.setCountry(auction.getCountry());
        dto.setBidCount(Math.toIntExact(auction.getBidCount()));

        SellerSummaryDTO sellerDto = new SellerSummaryDTO();
        sellerDto.setId(auction.getSeller().getId());
        sellerDto.setName(auction.getSeller().getFirstName());
        sellerDto.setLastName(auction.getSeller().getLastName());
        dto.setSeller(sellerDto);

        Set<CategorySummaryDTO> categoryDtos = auction.getCategories().stream()
                .map(cat -> {
                    CategorySummaryDTO cDto = new CategorySummaryDTO();
                    cDto.setId(cat.getId());
                    cDto.setName(cat.getName());
                    return cDto;
                }).collect(Collectors.toSet());

        dto.setCategories(categoryDtos);

        List<BidDTO> bidDtos = bidRepository.findByAuctionId(id).stream()
                .map(bid -> {
                    BidDTO bDto = new BidDTO();
                    bDto.setId(bid.getId());
                    bDto.setAmount(bid.getAmount());
                    bDto.setTime(bid.getTime());
                    bDto.setAuctionId(bid.getAuction().getId());
                    bDto.setBidderId(bid.getBidder().getId());
                    bDto.setBidderUsername(bid.getBidder().getUsername());
                    return bDto;
                })
                .toList();

        dto.setBids(bidDtos);

        return dto;
    }
}
