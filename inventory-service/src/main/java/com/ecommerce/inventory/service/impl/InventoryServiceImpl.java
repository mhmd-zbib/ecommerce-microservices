package com.ecommerce.inventory.service.impl;

import com.ecommerce.common.exception.ApiException;
import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryLockService lockService;
    
    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode, Integer quantity) {
        log.info("Checking stock for sku: {}", skuCode);
        return inventoryRepository.findBySkuCode(skuCode)
            .map(inventory -> inventory.getQuantity() >= quantity)
            .orElse(false);
    }
    
    @Override
    @Transactional
    public void updateStock(String skuCode, Integer quantity) {
        lockService.acquireLock(skuCode);
        try {
            Inventory inventory = findInventoryOrThrow(skuCode);
            validateStock(inventory, quantity);
            
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);
        } finally {
            lockService.releaseLock(skuCode);
        }
    }
    
    private Inventory findInventoryOrThrow(String skuCode) {
        return inventoryRepository.findBySkuCodeWithLock(skuCode)
            .orElseThrow(() -> new ApiException(
                "Inventory not found",
                "INVENTORY_NOT_FOUND",
                HttpStatus.NOT_FOUND
            ));
    }
    
    private void validateStock(Inventory inventory, Integer quantity) {
        if (inventory.getQuantity() < quantity) {
            throw new ApiException(
                "Insufficient stock",
                "INSUFFICIENT_STOCK",
                HttpStatus.BAD_REQUEST
            );
        }
    }
} 