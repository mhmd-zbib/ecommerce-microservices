package dev.zbib.productservice.repository;

import dev.zbib.productservice.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
//    Page<Product> findByCategory(String category, Pageable pageable);
//
//    Page<Product> findByCategoryAndName(String category, String name, Pageable pageable);
}
