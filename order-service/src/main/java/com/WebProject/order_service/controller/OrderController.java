package com.WebProject.order_service.controller;

import com.WebProject.order_service.dto.OrderRequest;
import com.WebProject.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    /*@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest)
    {
        orderService.placeOrder(orderRequest);
        return "Order Placed Succesfully";
    }*/
    /*@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
     //si inventory est en panne il execute la methode fallbackMehod
    public String placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        if (orderRequest.getOrderLineItemsDtoList() == null || orderRequest.getOrderLineItemsDtoList().isEmpty()) {
            throw new IllegalArgumentException("The orderLineItemsDtoList cannot be null or empty");
        }
        orderService.placeOrder(orderRequest);
        return "Order Placed Successfully";
    }*/
    @PostMapping
    public Mono<ResponseEntity<String>> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        if (orderRequest.getOrderLineItemsDtoList() == null || orderRequest.getOrderLineItemsDtoList().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("The orderLineItemsDtoList cannot be null or empty")); // Retourne une erreur 400
        }

        return orderService.placeOrder(orderRequest)
                .timeout(Duration.ofSeconds(5)) // Ajoute un timeout pour éviter les blocages infinis
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("Order Placed Successfully"))
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage()))) // Gère les exceptions spécifiques
                .onErrorResume(Exception.class, e -> {
                    // Log the full exception for debugging

                    log.error("Error placing order", e);
                    return Mono.just(ResponseEntity.internalServerError().body("Error placing order"));
                }); // Gère les autres exceptions et log l'erreur
    }



}
