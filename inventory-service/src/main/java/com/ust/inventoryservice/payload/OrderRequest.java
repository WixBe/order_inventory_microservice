package com.ust.inventoryservice.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record OrderRequest(
        @JsonProperty("OrderId") Long orderId,
        @JsonProperty("orderItems") List<OrderItemRequest> orderItems) implements Serializable {}

