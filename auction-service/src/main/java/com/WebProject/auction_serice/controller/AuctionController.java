package com.WebProject.auction_serice.controller;

import com.WebProject.auction_serice.dto.AuctionRequest;
import com.WebProject.auction_serice.model.Auction;
import com.WebProject.auction_serice.model.Bid;
import com.WebProject.auction_serice.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auction")
@RequiredArgsConstructor
public class AuctionController {

    private static final Logger log = LoggerFactory.getLogger(AuctionController.class);
    private final AuctionService auctionService;

    /*@PostMapping("/create")
    public ResponseEntity<Auction> createAuction(@RequestParam String productCode,
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Auction auction = auctionService.createAuction(productCode, endTime);
        return ResponseEntity.ok(auction);
    }*/
    /*@PostMapping("/create")
    public ResponseEntity<Auction> createAuction(@RequestBody AuctionRequest request) {
        Auction auction = auctionService.createAuction(request.getProductCode(), request.getEndTime());
        return ResponseEntity.ok(auction);
    }*/
    @PostMapping("/auction")
    public ResponseEntity<?> createAuction(@RequestBody AuctionRequest auctionRequest) {
        try {
            auctionService.createAuction(auctionRequest);
            return ResponseEntity.ok("Auction created successfully");
        } catch (RuntimeException e) {

            log.error("Error creating auction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating auction: " + e.getMessage());
        }
    }


    @PostMapping("/bid")
    public ResponseEntity<String> placeBid(@RequestParam Long auctionId,
                                           @RequestParam String bidder,
                                           @RequestParam Double amount) {
        try {
            String response = auctionService.placeBid(auctionId, bidder, amount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error placing bid: " + e.getMessage());
        }
    }


    @GetMapping("/{auctionId}/bids")
    public ResponseEntity<List<Bid>> getBidsForAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(auctionService.getBidsForAuction(auctionId));
    }

    @PostMapping("/{auctionId}/close")
    public ResponseEntity<String> closeAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(auctionService.closeAuction(auctionId));
    }
}

