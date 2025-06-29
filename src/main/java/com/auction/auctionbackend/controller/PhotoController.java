package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.model.Auction;
import com.auction.auctionbackend.model.Photo;
import com.auction.auctionbackend.repository.AuctionRepository;
import com.auction.auctionbackend.repository.PhotoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoRepository photoRepository;
    private final AuctionRepository auctionRepository;
    public PhotoController(PhotoRepository photoRepository, AuctionRepository auctionRepository) {
        this.photoRepository = photoRepository;
        this.auctionRepository = auctionRepository;
    }

    @PostMapping
    public ResponseEntity<?> addPhoto(@RequestParam Long auctionId, @RequestParam String url) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        Photo photo = new Photo();
        photo.setUrl(url);
        photo.setAuction(auction);

        return ResponseEntity.ok(photoRepository.save(photo));
    }
    //get the photos of a specific auction
    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<Photo>> getPhotosByAuction(@PathVariable Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        List<Photo> photos = photoRepository.findByAuctionId(auctionId);
        return ResponseEntity.ok(photos);
    }
}
