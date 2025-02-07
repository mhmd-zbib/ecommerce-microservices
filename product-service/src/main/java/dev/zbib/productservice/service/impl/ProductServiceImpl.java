package dev.zbib.productservice.service.impl;

import dev.zbib.common.exception.ApiException;
import dev.zbib.productservice.mapper.ProductMapper;
import dev.zbib.productservice.model.Product;
import dev.zbib.productservice.repository.ProductRepository;
import dev.zbib.productservice.service.ProductService;
import dev.zbib.productservice.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductValidator productValidator;
    
    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        productValidator.validateCreateRequest(request);
        
        Product product = productMapper.toEntity(request);
        product = productRepository.save(product);
        
        return productMapper.toResponse(product);
    }
    
    @Override
    public ProductResponse getProductById(String id) {
        Product product = findProductOrThrow(id);
        return productMapper.toResponse(product);
    }
    
    private Product findProductOrThrow(String id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ApiException(
                "Product not found",
                "PRODUCT_NOT_FOUND",
                HttpStatus.NOT_FOUND
            ));
    }
} 