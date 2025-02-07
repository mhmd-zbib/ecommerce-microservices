package dev.zbib.inventoryservice.service.impl;

import dev.zbib.common.exception.ApiException;
import dev.zbib.inventoryservice.model.Inventory;
import dev.zbib.inventoryservice.repository.InventoryRepository;
import dev.zbib.inventoryservice.service.InventoryService;
import dev.zbib.inventoryservice.service.InventoryLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    
    @Override
    public List<InventoryResponse> checkInventory(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
            .map(inventory -> InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .quantity(inventory.getQuantity())
                .build())
            .toList();
    }
} 