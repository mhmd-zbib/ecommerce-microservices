package dev.zbib.orderservice.model.response;

import dev.zbib.orderservice.exceptions.ErrorResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class OutOfStockResponse extends ErrorResponse {

    private List<String> outOfStockSkuCodes;


}

