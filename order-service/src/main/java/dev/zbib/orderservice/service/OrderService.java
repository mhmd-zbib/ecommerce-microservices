package dev.zbib.orderservice.service;

import dev.zbib.orderservice.model.entity.Order;
import dev.zbib.orderservice.model.entity.OrderLineItems;
import dev.zbib.orderservice.model.request.OrderLineItemsRequest;
import dev.zbib.orderservice.model.request.OrderRequest;
import dev.zbib.orderservice.model.response.InventoryResponse;
import dev.zbib.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    @Transactional(readOnly = true)
    public void placeOrder(OrderRequest request) {
        final List<OrderLineItems> orderLineItems = request.getOrderLineItemsRequestList()
                .stream()
                .map(this::mapToEntity)
                .toList();

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderLineItems)
                .build();

        final List<String> skuCodes = order.getOrderLineItems().stream().
                map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8003/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        assert inventoryResponseArray != null;
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

        if (!allProductsInStock) {
            throw new IllegalArgumentException("Product not in stock please try again later.");
        }

        orderRepository.save(order);
    }

    private OrderLineItems mapToEntity(OrderLineItemsRequest orderLineItemsRequest) {
        return OrderLineItems.builder()
                .price(orderLineItemsRequest.getPrice())
                .quantity(orderLineItemsRequest.getQuantity())
                .skuCode(orderLineItemsRequest.getSkuCode())
                .build();
    }

}
