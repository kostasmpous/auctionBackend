package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.dto.*;
import com.auction.auctionbackend.model.Auction;
import com.auction.auctionbackend.model.User;
import com.auction.auctionbackend.repository.AuctionRepository;
import com.auction.auctionbackend.repository.BidRepository;
import com.auction.auctionbackend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;

    public AuctionController(AuctionRepository auctionRepository, BidRepository bidRepository,UserRepository userRepository ) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
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
        sellerDto.setRating(auction.getSeller().getRatingAsSeller());
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

    @DeleteMapping("/{id}")
    public void deleteAuction(@PathVariable Long id, Principal principal) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        // Check if current user is the seller
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!auction.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to delete this auction");
        }

        // Check if there are bids or if auction already started
        boolean hasBids = bidRepository.existsByAuctionId(id);
        boolean hasStarted = auction.getStartTime().isBefore(java.time.LocalDateTime.now());

        if (hasBids || hasStarted) {
            throw new RuntimeException("Cannot delete auction that has already started or has bids.");
        }

        auctionRepository.delete(auction);
    }

    @GetMapping("/my-auctions")
    public List<AuctionListDTO> getMyAuctions(Principal principal) {
        String username = principal.getName();

        // Fetch the seller (user)
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch auctions created by this seller
        List<Auction> auctions = auctionRepository.findBySellerIdWithCategories(seller.getId());

        // Map to DTO
        return auctions.stream().map(auction -> {
            AuctionListDTO dto = new AuctionListDTO();
            dto.setId(auction.getId());
            dto.setName(auction.getName());
            dto.setDescription(auction.getDescription());
            dto.setStartTime(auction.getStartTime());
            dto.setEndTime(auction.getEndTime());
            dto.setCurrentPrice(auction.getCurrentPrice());
            dto.setBidCounts(auction.getBidCount());
            dto.setStartPrice(auction.getStartingPrice());

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
            return dto;
        }).toList();
    }

    @PutMapping("/{id}")
    public Auction editAuction(@PathVariable Long id, @RequestBody Auction updatedAuction, Principal principal) {
        // Get current user from JWT
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Auction existingAuction = auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        // Allow only the seller to update
        if (!existingAuction.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to edit this auction");
        }

        // Allow edit only if auction has not ended
        if (existingAuction.getEndTime().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Cannot edit an auction that has already ended");
        }

        // Update only description and endTime
        existingAuction.setDescription(updatedAuction.getDescription());
        existingAuction.setEndTime(updatedAuction.getEndTime());

        return auctionRepository.save(existingAuction);
    }


}
