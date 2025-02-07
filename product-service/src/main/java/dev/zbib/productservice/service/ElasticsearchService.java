package dev.zbib.productservice.service.impl;

import dev.zbib.productservice.dto.ProductResponse;
import dev.zbib.productservice.model.elasticsearch.ProductDocument;
import dev.zbib.productservice.repository.elasticsearch.ProductSearchRepository;
import dev.zbib.productservice.service.SearchService;
import dev.zbib.productservice.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchService implements SearchService {
    private final ProductSearchRepository searchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponse> search(String query, PageRequest pageRequest) {
        log.debug("Searching products with query: {}", query);
        
        NativeSearchQuery searchQuery = createSearchQuery(query, pageRequest);
        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(
                searchQuery, ProductDocument.class);
        
        return createSearchResult(searchHits, pageRequest);
    }

    @Override
    public void index(ProductResponse product) {
        log.debug("Indexing product: {}", product.getId());
        ProductDocument document = productMapper.toDocument(product);
        searchRepository.save(document);
    }

    @Override
    public void deleteFromIndex(String id) {
        log.debug("Deleting product from index: {}", id);
        searchRepository.deleteById(id);
    }

    private NativeSearchQuery createSearchQuery(String query, PageRequest pageRequest) {
        return new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(query)
                        .field("name", 2.0f)
                        .field("description")
                        .type("best_fields")
                        .fuzziness("AUTO"))
                .withPageable(pageRequest)
                .build();
    }

    private Page<ProductResponse> createSearchResult(SearchHits<ProductDocument> searchHits, 
                                                   PageRequest pageRequest) {
        List<ProductResponse> products = searchHits.get()
                .map(hit -> productMapper.toResponse(hit.getContent()))
                .collect(Collectors.toList());
                
        return new PageImpl<>(products, pageRequest, searchHits.getTotalHits());
    }
} 