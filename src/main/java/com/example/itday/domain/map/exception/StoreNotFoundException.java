package com.example.itday.domain.map.exception;

public class StoreNotFoundException extends RuntimeException {

    public StoreNotFoundException(Long storeId) {
        super("Store not found: " + storeId);
    }
}
