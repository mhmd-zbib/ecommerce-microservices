package dev.zbib.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequest {
    @NotBlank(message = "SKU code is required")
    private String skuCode;
    
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
} 