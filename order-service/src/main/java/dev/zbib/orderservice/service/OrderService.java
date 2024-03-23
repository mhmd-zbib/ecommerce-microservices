package dev.zbib.orderservice.service;

import dev.zbib.orderservice.model.entity.Order;
import dev.zbib.orderservice.model.entity.OrderLineItems;
import dev.zbib.orderservice.model.request.OrderLineItemsRequest;
import dev.zbib.orderservice.model.request.OrderRequest;
import dev.zbib.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

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
