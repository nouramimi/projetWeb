package com.WebProject.auction_serice.repository;

import com.WebProject.auction_serice.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Optional<Auction> findByProductCode(String productCode);
    List<Auction> findByEndTimeBefore(LocalDateTime currentTime);
}
