package dev.zbib.inventoryservice.model.response;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
private String skuCode;
private boolean isInStock;
}
