package dev.zbib.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
} 