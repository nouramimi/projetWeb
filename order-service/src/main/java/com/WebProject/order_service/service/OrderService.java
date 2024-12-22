package com.WebProject.order_service.service;

import com.WebProject.order_service.dto.InventoryResponse;
import com.WebProject.order_service.dto.OrderLineItemsDto;
import com.WebProject.order_service.dto.OrderRequest;
import com.WebProject.order_service.event.OrderPlacedEvent;
import com.WebProject.order_service.model.Order;
import com.WebProject.order_service.model.OrderLineItems;
import com.WebProject.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    public Mono<Void> placeOrder(OrderRequest orderRequest) {
        // Vérification si la liste d'articles de la commande est vide ou nulle
        if (orderRequest.getOrderLineItemsDtoList() == null || orderRequest.getOrderLineItemsDtoList().isEmpty()) {
            return Mono.error(new IllegalArgumentException("La demande de commande doit contenir des articles de commande"));
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        // Mapping des articles de la commande
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);

        // Traitement du stock
        List<String> skuCodes = orderLineItems.stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // Appel au service d'inventaire pour vérifier la disponibilité des produits
        return webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        clientResponse -> Mono.error(new RuntimeException("Erreur côté client lors de la vérification du stock"))
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("Erreur côté serveur lors de la vérification du stock"))
                )
                .bodyToMono(InventoryResponse[].class)
                .flatMap(inventoryResponseArray -> {
                    if (inventoryResponseArray == null || inventoryResponseArray.length == 0) {
                        return Mono.error(new RuntimeException("Aucune réponse d'inventaire reçue du service"));
                    }

                    // Vérification de la disponibilité de tous les produits
                    boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                            .allMatch(InventoryResponse::isInStock);

                    if (allProductsInStock) {
                        // Sauvegarde de la commande dans la base de données
                        orderRepository.save(order);
                        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber());
                        kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));

                        log.info("Order placed successfully: {}", order.getOrderNumber());
                        //log.info("Commande passée avec succès : {}", order.getOrderNumber());
                        return Mono.empty();
                    } else {
                        // Si des produits ne sont pas en stock, on lève une exception
                        log.error("Commande échouée, produits non disponibles en stock pour la commande : {}", order.getOrderNumber());
                        return Mono.error(new IllegalArgumentException("Produit non disponible en stock, veuillez réessayer"));
                    }
                });
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
