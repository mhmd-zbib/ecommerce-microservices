package dev.zbib.productservice.validator;

import com.ecommerce.common.exception.ApiException;
import com.ecommerce.product.dto.ProductRequest;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;

@Component
public class ProductValidator {
    
    public void validateCreateRequest(ProductRequest request) {
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException(
                "Price must be greater than zero",
                "INVALID_PRICE",
                HttpStatus.BAD_REQUEST
            );
        }
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ApiException(
                "Product name is required",
                "INVALID_NAME",
                HttpStatus.BAD_REQUEST
            );
        }
    }
} 