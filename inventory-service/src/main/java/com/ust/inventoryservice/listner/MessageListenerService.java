package com.ust.inventoryservice.listner;

import com.ust.inventoryservice.config.RabbitConfig;
import com.ust.inventoryservice.payload.OrderRequest;
import com.ust.inventoryservice.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageListenerService {

    private final ProductService ps;
    private final ProductService productService;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME, messageConverter = "receiverJackson2MessageConverter")
    public void listenMessage(OrderRequest message) {
        log.info("Received message: {}", message);
        productService.processOrder(message);
    }
}