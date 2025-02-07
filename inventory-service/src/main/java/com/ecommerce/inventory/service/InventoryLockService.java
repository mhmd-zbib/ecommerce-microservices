package com.ecommerce.inventory.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class InventoryLockService {
    private final ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();
    
    public void acquireLock(String skuCode) {
        locks.computeIfAbsent(skuCode, k -> new ReentrantLock()).lock();
    }
    
    public void releaseLock(String skuCode) {
        Lock lock = locks.get(skuCode);
        if (lock != null) {
            lock.unlock();
        }
    }
} 