package com.ust.inventoryservice.payload;

public record UpResponseDto(
        String skuCode,
        Integer quantity,
        Double price
) {
}
