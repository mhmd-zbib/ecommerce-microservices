package dev.zbib.orderservice.service;

import dev.zbib.common.exception.ApiException;
import dev.zbib.orderservice.client.InventoryClient;
import dev.zbib.orderservice.dto.OrderRequest;
import dev.zbib.orderservice.dto.OrderResponse;
import dev.zbib.orderservice.mapper.OrderMapper;
import dev.zbib.orderservice.model.Order;
import dev.zbib.orderservice.model.entity.OrderStatus;
import dev.zbib.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final InventoryClient inventoryClient;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating new order");
        
        if (!inventoryClient.checkInventory(request.getOrderItems())) {
            throw new ApiException(
                "One or more items are out of stock",
                "INSUFFICIENT_STOCK",
                HttpStatus.BAD_REQUEST
            );
        }
        
        Order order = orderMapper.toEntity(request);
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);
        
        try {
            inventoryClient.updateInventory(request.getOrderItems());
            order.setStatus(OrderStatus.CONFIRMED);
            order = orderRepository.save(order);
        } catch (Exception e) {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            throw new ApiException(
                "Failed to process order",
                "ORDER_PROCESSING_FAILED",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        
        return orderMapper.toResponse(order);
    }

    public OrderResponse getOrder(String orderNumber) {
        Order order = findOrderOrThrow(orderNumber);
        return orderMapper.toResponse(order);
    }

    @Transactional
    public void cancelOrder(String orderNumber) {
        Order order = findOrderOrThrow(orderNumber);
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ApiException(
                "Order cannot be cancelled",
                "INVALID_ORDER_STATUS",
                HttpStatus.BAD_REQUEST
            );
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private Order findOrderOrThrow(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ApiException(
                "Order not found",
                "ORDER_NOT_FOUND",
                HttpStatus.NOT_FOUND
            ));
    }
}
