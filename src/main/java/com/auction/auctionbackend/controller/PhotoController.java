package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.model.Photo;
import com.auction.auctionbackend.repository.PhotoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoRepository photoRepository;

    public PhotoController(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @PostMapping
    public Photo createPhoto(@RequestBody Photo photo) {
        return photoRepository.save(photo);
    }

    @GetMapping
    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Photo getPhotoById(@PathVariable Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Photo not found"));
    }
}
