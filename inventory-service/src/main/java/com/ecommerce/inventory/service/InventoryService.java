package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import java.util.List;

public interface InventoryService {
    boolean isInStock(String skuCode, Integer quantity);
    void updateStock(String skuCode, Integer quantity);
    List<InventoryResponse> checkInventory(List<String> skuCodes);
} 