package dev.zbib.orderservice.service;

import dev.zbib.orderservice.exceptions.OutOfStockException;
import dev.zbib.orderservice.model.entity.Order;
import dev.zbib.orderservice.model.entity.OrderItem;
import dev.zbib.orderservice.model.enums.OrderStatus;
import dev.zbib.orderservice.model.request.OrderItemRequest;
import dev.zbib.orderservice.model.request.OrderRequest;
import dev.zbib.orderservice.model.response.InventoryResponse;
import dev.zbib.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    @Transactional
    public String placeOrder(OrderRequest orderRequest) {

        List<OrderItem> orderItems = orderRequest.getOrderItems().stream().map(this::mapToOrderItem).toList();

        validateStockAvailability(orderItems);

        Order order = Order.builder().orderNumber(UUID.randomUUID().toString()).orderItems(orderItems).status(OrderStatus.PENDING).orderDate(LocalDateTime.now()).build();

        orderRepository.save(order);
        return order.getOrderNumber();
    }

    private void validateStockAvailability(List<OrderItem> orderItems) {
        Map<String, Integer> requiredQuantities = orderItems.stream().collect(Collectors.toMap(OrderItem::getSkuCode, OrderItem::getQuantity));

        List<InventoryResponse> inventoryResponses = inventoryService.checkStock(new ArrayList<>(requiredQuantities.keySet()));

        List<String> outOfStockSkuCodes = new ArrayList<>();
        List<String> insufficientStockSkuCodes = new ArrayList<>();

        for (InventoryResponse response : inventoryResponses) {
            Integer requiredQuantity = requiredQuantities.get(response.getSkuCode());
            if (response.getQuantityInStock() == 0) {
                outOfStockSkuCodes.add(response.getSkuCode());
            } else if (requiredQuantity != null && requiredQuantity > response.getQuantityInStock()) {
                insufficientStockSkuCodes.add(response.getSkuCode());
            }
        }

        if (!outOfStockSkuCodes.isEmpty()) {
            throw new OutOfStockException(insufficientStockSkuCodes);
        }

        if (!insufficientStockSkuCodes.isEmpty()) {
            throw new OutOfStockException(insufficientStockSkuCodes);
        }

    }

    private OrderItem mapToOrderItem(OrderItemRequest orderItemsRequest) {
        return OrderItem.builder().quantity(orderItemsRequest.getQuantity()).skuCode(orderItemsRequest.getSkuCode()).build();
    }
}
