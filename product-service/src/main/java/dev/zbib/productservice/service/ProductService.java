package dev.zbib.productservice.service;

import dev.zbib.common.exception.ApiException;
import dev.zbib.productservice.dto.ProductRequest;
import dev.zbib.productservice.dto.ProductResponse;
import dev.zbib.productservice.mapper.ProductMapper;
import dev.zbib.productservice.model.Product;
import dev.zbib.productservice.repository.ProductRepository;
import dev.zbib.productservice.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductValidator productValidator;
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        productValidator.validateCreateRequest(request);
        
        Product product = productMapper.toEntity(request);
        product = productRepository.save(product);
        
        return productMapper.toResponse(product);
    }
    
    public ProductResponse getProductById(String id) {
        Product product = findProductOrThrow(id);
        return productMapper.toResponse(product);
    }
    
    public List<ProductResponse> getAllProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size))
            .map(productMapper::toResponse)
            .getContent();
    }
    
    @Transactional
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ApiException(
                "Product not found",
                "PRODUCT_NOT_FOUND",
                HttpStatus.NOT_FOUND
            );
        }
        productRepository.deleteById(id);
    }
    
    @Transactional
    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product product = findProductOrThrow(id);
        
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        
        product = productRepository.save(product);
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