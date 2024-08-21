package com.ust.orderservice.payload;

import org.springframework.http.HttpStatus;

public record Product(
        String skuCode,
        boolean available,
        HttpStatus status
) {
}
