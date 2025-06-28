package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.dto.*;
import com.auction.auctionbackend.model.Auction;
import com.auction.auctionbackend.model.AuctionStatus;
import com.auction.auctionbackend.model.Category;
import com.auction.auctionbackend.model.User;
import com.auction.auctionbackend.repository.AuctionRepository;
import com.auction.auctionbackend.repository.BidRepository;
import com.auction.auctionbackend.repository.CategoryRepository;
import com.auction.auctionbackend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    public AuctionController(AuctionRepository auctionRepository, BidRepository bidRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public Auction createAuction(@RequestBody AuctionCreateDTO dto, Principal principal) {
        String username = principal.getName();
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Auction auction = new Auction();
        auction.setName(dto.getName());
        auction.setDescription(dto.getDescription());
        auction.setStartTime(dto.getStartTime());
        auction.setEndTime(dto.getEndTime());
        auction.setStartingPrice(dto.getStartingPrice());
        auction.setLocation(dto.getLocation());
        auction.setCountry(dto.getCountry());
        auction.setSeller(seller);

        Set<Category> categories = dto.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + id)))
                .collect(Collectors.toSet());

        auction.setCategories(categories);
        // Determine status based on current time
        LocalDateTime now = LocalDateTime.now();
        if (dto.getStartTime().isAfter(now)) {
            auction.setStatus(AuctionStatus.UPCOMING);
        } else if (dto.getEndTime().isAfter(now)) {
            auction.setStatus(AuctionStatus.ACTIVE);
        } else {
            auction.setStatus(AuctionStatus.ENDED); // Shouldn't normally happen on creation
        }
        return auctionRepository.save(auction);
    }




    @GetMapping
    public List<AuctionListDTO> getAllAuctions() {
        return auctionRepository.findAllWithCategoriesAndSeller().stream()
                .filter(auction -> auction.getStatus() != AuctionStatus.ENDED) // Exclude ended
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
                    dto.setStatus(auction.getStatus());
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
        dto.setStatus(auction.getStatus());

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

    @GetMapping("/public")
    public List<AuctionMinimalDTO> getPublicAuctions() {
        return auctionRepository.findAllWithCategoriesAndSeller().stream().map(auction -> {
            AuctionMinimalDTO dto = new AuctionMinimalDTO();
            dto.setId(auction.getId());
            dto.setName(auction.getName());
            dto.setDescription(auction.getDescription());
            dto.setStartingPrice(auction.getStartingPrice());
            dto.setCurrentPrice(auction.getCurrentPrice());
            dto.setBidCount(auction.getBidCount());
            dto.setStartTime(auction.getStartTime());
            dto.setEndTime(auction.getEndTime());
            return dto;
        }).toList();
    }



}
