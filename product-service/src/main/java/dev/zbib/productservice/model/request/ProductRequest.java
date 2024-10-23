package dev.zbib.productservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
@NonNull
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
}
