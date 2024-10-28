package dev.zbib.orderservice.service;

import dev.zbib.orderservice.model.response.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final WebClient webClient;

    public List<InventoryResponse> checkStock(List<String> skuCodes) {
        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://localhost:8003/inventory", uriBuilder ->
                        uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse ->
                        Mono.error(new RuntimeException("Inventory service is unavailable.")))
                .bodyToMono(InventoryResponse[].class)
                .block();
        return inventoryResponses != null ? Arrays.asList(inventoryResponses) : List.of();
    }
}
