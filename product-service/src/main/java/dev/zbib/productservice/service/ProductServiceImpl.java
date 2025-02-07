package dev.zbib.productservice.service.impl;

import dev.zbib.common.exception.ApiException;
import dev.zbib.productservice.dto.ProductRequest;
import dev.zbib.productservice.dto.ProductResponse;
import dev.zbib.productservice.mapper.ProductMapper;
import dev.zbib.productservice.model.Product;
import dev.zbib.productservice.repository.ProductRepository;
import dev.zbib.productservice.service.ProductService;
import dev.zbib.productservice.service.SearchService;
import dev.zbib.productservice.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductValidator productValidator;
    private final SearchService searchService;
    
    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        productValidator.validateCreateRequest(request);
        
        Product product = productMapper.toEntity(request);
        product = productRepository.save(product);
        
        ProductResponse response = productMapper.toResponse(product);
        searchService.index(response);
        
        return response;
    }
    
    @Override
    public ProductResponse getProductById(String id) {
        Product product = findProductOrThrow(id);
        return productMapper.toResponse(product);
    }
    
    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
            .map(productMapper::toResponse);
    }
    
    @Override
    @Transactional
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ApiException("Product not found", "PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        productRepository.deleteById(id);
        searchService.deleteFromIndex(id);
    }
    
    @Override
    @Transactional
    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product product = findProductOrThrow(id);
        
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        
        product = productRepository.save(product);
        ProductResponse response = productMapper.toResponse(product);
        searchService.index(response);
        
        return response;
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