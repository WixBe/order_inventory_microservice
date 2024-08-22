package com.ust.inventoryservice.listner;

import com.ust.inventoryservice.config.RabbitConfig;
import com.ust.inventoryservice.payload.OrderRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListner {

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void listenMessage(OrderRequest message) {
        System.out.println("Message received: " + message);
    }
}
