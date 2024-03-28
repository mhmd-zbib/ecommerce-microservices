package dev.zbib.orderservice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OutOfStockException extends RuntimeException {

    private List<String> outOfStockSkuCodes;

    @Override
    public String getMessage() {
        return "The following products are out of stock";
    }
}