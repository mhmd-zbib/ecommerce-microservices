package dev.zbib.orderservice.controller;

import dev.zbib.orderservice.model.request.OrderRequest;
import dev.zbib.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> addOrder(@RequestBody OrderRequest order) {
        orderService.placeOrder(order);
        return ResponseEntity.ok("Success");
    }
}
