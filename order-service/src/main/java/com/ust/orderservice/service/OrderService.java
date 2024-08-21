package com.ust.orderservice.service;

import com.ust.orderservice.client.InventoryServiceClient;
import com.ust.orderservice.domain.Order;
import com.ust.orderservice.payload.Product;
import com.ust.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final InventoryServiceClient client;

    public Order createOrder(Order order) {
        order = orderRepository.save(order);
        return order;
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow();
    }
}
