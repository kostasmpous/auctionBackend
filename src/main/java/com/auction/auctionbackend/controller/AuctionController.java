package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.model.Auction;
import com.auction.auctionbackend.repository.AuctionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.auction.auctionbackend.dto.AuctionListDTO;
import com.auction.auctionbackend.dto.SellerSummaryDTO;
import com.auction.auctionbackend.dto.CategorySummaryDTO;

import java.util.Set;
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionRepository auctionRepository;

    public AuctionController(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @PostMapping
    public Auction createAuction(@RequestBody Auction auction) {
        return auctionRepository.save(auction);
    }



    @Transactional(readOnly = true)
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
    public Auction getAuctionById(@PathVariable Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
    }
}
