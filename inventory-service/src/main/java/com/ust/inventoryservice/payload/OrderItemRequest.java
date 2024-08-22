package com.ust.inventoryservice.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record OrderItemRequest(
        @JsonProperty("skuCode") String skuCode,
        @JsonProperty("quantity") Integer quantity
) implements Serializable {
}
