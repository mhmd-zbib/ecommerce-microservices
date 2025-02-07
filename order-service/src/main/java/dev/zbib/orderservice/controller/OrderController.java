package dev.zbib.orderservice.controller;

import dev.zbib.orderservice.model.request.OrderRequest;
import dev.zbib.orderservice.model.response.OrderResponse;
import dev.zbib.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @PostMapping
//    @CircuitBreaker(name = "inventory", fallbackMethod = "fallBackMethod")
    public ResponseEntity<?> place(@RequestBody OrderRequest order) {
        String orderNumber = orderService.placeOrder(order);
        return ResponseEntity.ok(
                OrderResponse.builder()
                        .orderNumber(orderNumber)
                        .message("Order has been placed successfully")
                        .build()
        );
    }

    public ResponseEntity<?> fallBackMethod(OrderRequest order, Throwable e) {
        log.error("Fallback method invoked: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("The inventory service is currently unavailable. Please try again later.");
    }
}
