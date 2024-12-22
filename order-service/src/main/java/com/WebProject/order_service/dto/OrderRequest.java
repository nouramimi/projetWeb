package com.WebProject.order_service.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @NotEmpty(message = "OrderLineItemsDtoList cannot be empty")
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
