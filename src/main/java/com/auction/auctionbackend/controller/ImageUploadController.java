package com.auction.auctionbackend.controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {
    //Client ID and URL we got from IMGUR to use it as a repository for our uploaded photos and fetch them later in the app
    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";
    private static final String IMGUR_CLIENT_ID = "39439e60afc78fb";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadToImgur(@RequestParam("file") MultipartFile file) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Client-ID " + IMGUR_CLIENT_ID);

            byte[] fileBytes = file.getBytes();
            String encoded = Base64.getEncoder().encodeToString(fileBytes);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("image", encoded);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    IMGUR_UPLOAD_URL, HttpMethod.POST, requestEntity, String.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}
