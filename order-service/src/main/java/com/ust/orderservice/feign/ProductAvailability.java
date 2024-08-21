package com.ust.orderservice.feign;

import org.springframework.http.HttpStatus;

public record ProductAvailability(
        String skuCode,
        boolean available,
        HttpStatus status
) {
}
