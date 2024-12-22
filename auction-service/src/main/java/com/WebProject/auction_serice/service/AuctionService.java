package com.WebProject.auction_serice.service;

import com.WebProject.auction_serice.model.Auction;
import com.WebProject.auction_serice.model.Bid;
import com.WebProject.auction_serice.repository.AuctionRepository;
import com.WebProject.auction_serice.repository.BidRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private static final Logger log = LoggerFactory.getLogger(AuctionService.class);
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final RestTemplate restTemplate; // Pour appeler les autres services
    private final KafkaTemplate<String, String> kafkaTemplate; // Pour notifications Kafka


    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${notification.topic}")
    private String notificationTopic;

    /*@Transactional
    public Auction createAuction(String productCode, LocalDateTime endTime) {
        try {
            // Vérifier les entrées
            if (productCode == null || productCode.isEmpty()) {
                throw new IllegalArgumentException("Product code is required");
            }

            // Créez un objet Auction et définissez les valeurs
            Auction auction = new Auction();
            auction.setProductCode(productCode);
            auction.setEndTime(endTime);
            auction.setStartTime(LocalDateTime.now());

            // Enregistrez l'enchère en base de données (si applicable)
            Auction savedAuction = auctionRepository.save(auction);
            return savedAuction;
        } catch (Exception e) {
            // Log l'exception pour faciliter le débogage

            log.error("Error creating auction: ", e);
            throw new RuntimeException("Error creating auction", e);
        }
    }*/
    @Transactional
    public Auction createAuction(String productCode, LocalDateTime endTime) {
        // Vérifiez si le produit existe via le service produit
        if (!isProductAvailable(productCode)) {
            throw new RuntimeException("Product with code " + productCode + " does not exist");
        }

        Auction auction = Auction.builder()
                .productCode(productCode)
                .endTime(endTime)
                .startTime(LocalDateTime.now())
                .build();

        return auctionRepository.save(auction);
    }



    @Transactional
    public String placeBid(Long auctionId, String bidder, Double amount) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new RuntimeException("Auction not found"));

        if (auction.getHighestBid() == null || amount > auction.getHighestBid()) {
            auction.setHighestBid(amount);
            auction.setHighestBidder(bidder);
            auctionRepository.save(auction);

            // Créez un nouvel objet Bid avec le timestamp actuel
            Bid newBid = new Bid(null, auctionId, bidder, amount, LocalDateTime.now());
            bidRepository.save(newBid);

            return "Bid placed successfully";
        }

        return "Bid amount must be higher than the current highest bid";
    }



    public List<Bid> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuctionId(auctionId);
    }

    @Transactional
    public String closeAuction(Long auctionId) {
        log.info("Attempting to close auction with ID: {}", auctionId);

        try {
            Auction auction = auctionRepository.findById(auctionId)
                    .orElseThrow(() -> new RuntimeException("Auction not found"));

            log.info("Auction found: {}", auction);

            if (LocalDateTime.now().isBefore(auction.getEndTime())) {
                log.warn("Auction is still ongoing, cannot close");
                throw new RuntimeException("Auction is still ongoing");
            }

            String winner = auction.getHighestBidder();
            if (winner == null) {
                log.info("No winner found for this auction");
                return "No winner for this auction";
            }

            // Publier un événement Kafka pour notifier le gagnant
            //kafkaTemplate.send(notificationTopic, "Winner: " + winner + " for auction: " + auctionId);
            try {
                kafkaTemplate.send(notificationTopic, "Winner: " + winner + " for auction: " + auctionId);
            } catch (Exception e) {
                log.error("Error sending Kafka message for auction ID: {}", auctionId, e);
                throw new RuntimeException("Error sending Kafka message", e);
            }

            log.info("Auction closed! Winner: {} with bid: ${}", winner, auction.getHighestBid());

            return "Auction closed! Winner: " + winner + " with bid: $" + auction.getHighestBid();
        } catch (Exception e) {
            log.error("Error closing auction with ID: {}", auctionId, e);
            throw new RuntimeException("Error closing auction", e);
        }
    }
    public boolean isProductAvailable(String productCode) {
        try {
            String url = "http://localhost:8080/api/product/" + productCode;
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return response.getBody() != null && response.getBody();
        } catch (Exception e) {
            log.error("Error while checking product availability: ", e);
            throw new RuntimeException("Unable to verify product availability");
        }
    }



}


