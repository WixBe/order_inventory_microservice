package com.ust.orderservice.feign;

public record Product(
        String skuCode,
        String name,
        double price,
        int quantity
) {
}
