package com.ust.orderservice.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record OrderRequest(
        @JsonProperty("orderItems") List<OrderItemRequest> orderItems) implements Serializable {
}
