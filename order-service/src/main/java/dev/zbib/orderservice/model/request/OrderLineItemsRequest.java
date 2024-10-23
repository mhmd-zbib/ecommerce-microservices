package dev.zbib.orderservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLineItemsRequest {

    private String skuCode;

    private BigDecimal price;

    private Integer quantity;

}
