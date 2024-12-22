package com.WebProject.auction_serice.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class AuctionRequest {
    private String productCode;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    // Getters et setters
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
