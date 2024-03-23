package dev.zbib.inventoryservice.repository;

import dev.zbib.inventoryservice.model.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findBySkuCode(String skuCode);
}
