package com.WebProject.auction_serice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_auction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productCode; // Code du produit associé à l'enchère.
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double highestBid;
    private String highestBidder; // Identifiant de l'utilisateur.
}

