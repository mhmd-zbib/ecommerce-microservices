package dev.zbib.inventoryservice.service;

import dev.zbib.common.exception.ApiException;
import dev.zbib.inventoryservice.dto.InventoryResponse;
import dev.zbib.inventoryservice.model.Inventory;
import dev.zbib.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryLockService lockService;
    
    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode, Integer quantity) {
        log.info("Checking stock for sku: {}", skuCode);
        return inventoryRepository.findBySkuCode(skuCode)
            .map(inventory -> inventory.getQuantity() >= quantity)
            .orElse(false);
    }
    
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
    
    public List<InventoryResponse> checkInventory(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
            .map(inventory -> InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .quantity(inventory.getQuantity())
                .build())
            .toList();
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
