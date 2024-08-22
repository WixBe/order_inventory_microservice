## Queue

In RabbitMQ, a queue is a fundamental component used to store and distribute messages. 
Queues act as buffers that hold messages sent by producers until they are consumed by consumers. 
They are a key part of RabbitMQ's messaging system, ensuring that messages are delivered to consumers in a 
reliable and scalable way.

### Key Concepts of Queues in RabbitMQ

**Message Storage:**
    Queues store messages that are sent by producers. Each message in a queue is delivered to one or more consumers, 
    depending on the type of exchange and binding used.

**Message Delivery:**
    Messages are delivered to consumers in the order they were added to the queue (FIFO - First In, First Out). 
    A message remains in the queue until a consumer acknowledges that it has received and processed the message.

**Durability:**
    Queues can be durable or non-durable.
    Durable queues survive RabbitMQ server restarts, ensuring that any messages in the queue at the time of a 
    restart are not lost.
    Non-durable queues are temporary and will be deleted when the RabbitMQ server stops.

**Exclusivity:**
    Exclusive queues can only be accessed by the connection that created them. These queues are automatically 
    deleted when the connection closes.
    Non-exclusive queues can be accessed by multiple connections.

**Auto-delete:**
    Queues can be configured with an auto-delete property. An auto-delete queue will be automatically deleted 
    when the last consumer unsubscribes from it.

**TTL (Time-To-Live):**
    Messages in a queue can have a TTL, which is the amount of time a message can stay in the queue before 
    it is discarded.

**Dead-Letter Queues:**
    Queues can be configured to send messages to a dead-letter queue if they are rejected or expire without 
    being consumed.

### Advanced Queue Features

**Priority Queues:** RabbitMQ supports message priority within queues, allowing more important messages to be 
processed first.

**Lazy Queues:** These are optimized to hold large volumes of messages by keeping them on disk as much as possible, 
rather than in memory.

**Quorum Queues:** A more reliable alternative to classic mirrored queues, designed to provide data safety and 
consistency using the Raft consensus algorithm.

### Use Cases for Queues

**Task Queues:** For background processing, where tasks are added to a queue by producers and workers (consumers) 
process them asynchronously.

**Work Distribution:** Distributing tasks among multiple workers to balance load and increase throughput.

**Event Notification:** Subscribing to events and triggering actions in response to specific messages.

**Decoupling Microservices:** Enabling microservices to communicate asynchronously, improving system resilience 
and scalability.

### Sample Code

**Creating a Queue**
```java
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue orderQueue() {
        // Creating a durable queue named "order.queue"
        return new Queue("order.queue", true);
    }
}
```

**Sending Messages to a Queue**
```java
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void placeOrder(Order order) {
        // Send the order to the "order.queue"
        rabbitTemplate.convertAndSend("order.queue", order);
    }
}
```

**Consuming Message From Queue**
```java
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {

    @RabbitListener(queues = "order.queue")
    public void receiveMessage(Order order) {
        // Process the received order message
        System.out.println("Received order: " + order);
    }
}
```

## Topic Exchange

A Topic Exchange in RabbitMQ is a type of exchange that routes messages to queues based on matching between a 
message's routing key and the pattern that was used to bind a queue to the exchange. This allows for more complex 
routing scenarios than a direct exchange, as the topic exchange can route messages to multiple queues based on 
wildcard matches.

### How a Topic Exchange Works

**Routing Key**: When a producer sends a message to a topic exchange, it includes a routing key, which is typically 
a dot-separated string (e.g., "order.created", "order.updated", etc.).

**Bindings**: Queues are bound to the topic exchange with a binding key, which is also a dot-separated string. 
Binding keys can contain wildcards:
`*` (asterisk) matches exactly one word.
`#` (hash) matches zero or more words.

Message Routing: When the topic exchange receives a message, it compares the message's routing key against the binding keys of all the queues bound to the exchange. If there's a match, the message is routed to the corresponding queue(s).

Example Scenario

Let's say you have a topic exchange named order.exchange and you have the following queues bound to it:

- Queue order.created.queue bound with the routing key order.created
- Queue order.updated.queue bound with the routing key order.updated
- Queue order.*.queue bound with the routing key order.*
- Queue order.#.queue bound with the routing key order.#

Now, consider the following scenarios:

**Routing Key order.created**
    This will match order.created.queue, order.*.queue, and order.#.queue.

**Routing Key order.updated**
    This will match order.updated.queue, order.*.queue, and order.#.queue.

**Routing Key order.deleted**
    This will match order.*.queue and order.#.queue (if such a routing key is used).

**Routing Key order.created.email**
    This will match only order.#.queue since # can match multiple words.

### Use Cases for Topic Exchanges

**Routing by Categories** 

You might use topic exchanges to route messages by different categories. 
For example, you could route all logs with a routing key pattern like logs.error.# 
to a specific queue for error logs.

**Complex Routing Rules** 

Topic exchanges are useful when you need to match messages based on 
complex routing rules, where different services might be interested in different parts of a message's routing key.

**Fanout with Filters** 

Unlike a fanout exchange, where every queue receives every message, 
a topic exchange can distribute messages selectively based on routing keys, 
making it more efficient when not all consumers need all messages.

### Sample Code

```java
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue("order.created.queue", false);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("order.exchange");
    }

    @Bean
    public Binding bindingOrderCreated(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with("order.created");
    }

    @Bean
    public Binding bindingOrderWildcard(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with("order.*");
    }
}

```

In this example, a queue named order.created.queue is bound to the topic exchange order.exchange with two different 
binding keys: order.created and order.*. This setup ensures 
that the queue receives messages with routing keys matching order.created or any routing key that starts with order.

## Binding

In RabbitMQ, a binding is the relationship between an exchange and a queue. It defines how messages routed by an 
exchange are delivered to one or more queues. A binding is essentially a rule that tells the exchange how to route 
messages based on the message's routing key.

### Key Concepts of Bindings in RabbitMQ

**Exchange:**
    An exchange receives messages from producers and is responsible for routing those messages to queues. 
    The type of exchange (direct, topic, fanout, or headers) determines the routing logic.

**Queue:**
    A queue stores messages until they are consumed by a consumer.

**Routing Key:**
    A routing key is a message attribute that the exchange uses to decide how to route the message. 
    The routing key is usually a string (e.g., "order.created", "payment.completed").

**Binding Key:**
    The binding key is a pattern used when creating a binding between an exchange and a queue. 
    The binding key determines whether a message with a specific routing key will be routed to the queue.

### Binding Types Based on Exchange

**Direct Exchange:**
    A binding key must exactly match the routing key for the message to be routed to the queue.
    Example: If a queue is bound to a direct exchange with the binding key "order.created", only messages with the routing key "order.created" will be routed to that queue.

**Topic Exchange:**
    Binding keys can include wildcards (* and #) for more flexible routing.
        * matches exactly one word.
        # matches zero or more words.
    Example: A binding key of "order.*" will match routing keys like "order.created" and "order.updated".

**Fanout Exchange:**
    All queues bound to a fanout exchange receive all messages, regardless of routing key. No binding key is used.
    Example: All messages sent to the fanout exchange are delivered to all bound queues.

**Headers Exchange:**
    Routing is based on message headers instead of the routing key. Binding keys are replaced by header key-value pairs.
    Example: Messages are routed based on the presence of specific headers like "type=order".