package dev.zbib.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.zbib.productservice.model.request.ProductRequest;
import dev.zbib.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//@Testcontainers
//@AutoConfigureMockMvc
class ProductServiceApplicationTests {
//
//    @Container
//    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.4");
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper mapper;
//    @Autowired
//    private ProductRepository productRepository;
//
//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
//    }
//
//    @Test
//    void shouldCreateProduct() throws Exception {
//        ProductRequest productRequest = getProductRequest();
//        String productRequestString = mapper.writeValueAsString(productRequest);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/product")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(productRequestString))
//                .andExpect(status().isCreated());
//        Assertions.assertEquals(1, productRepository.findAll().size());
//    }
//
//    private ProductRequest getProductRequest() {
//        return ProductRequest.builder()
//                .name("Test Product")
//                .description("Testing from test containers")
//                .price(BigDecimal.valueOf(999.99))
//                .build();
//    }

}
