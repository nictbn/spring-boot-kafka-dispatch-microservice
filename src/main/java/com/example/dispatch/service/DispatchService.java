package com.example.dispatch.service;

import com.example.message.DispatchPreparing;
import com.example.message.OrderCreated;
import com.example.message.OrderDispatched;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.util.UUID.randomUUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {
    public static final String ORDER_DISPATCHED_TOPIC = "order.dispatched";
    public static final String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";
    private static final UUID APPLICATION_ID = randomUUID();
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void process(String key, OrderCreated orderCreated) throws Exception {
        OrderDispatched orderDispatched = getOrderDispatchedEvent(orderCreated);
        sendOrderDispatchedEvent(key, orderDispatched);
        DispatchPreparing dispatchPreparingEvent = getDispatchPreparingEvent(orderCreated);
        sendDispatchPreparingEvent(key, dispatchPreparingEvent);
        log.info("Sent messages: key: " + key + " orderId: " + orderCreated.getOrderId() + " - processedById: " + APPLICATION_ID);
    }

    private OrderDispatched getOrderDispatchedEvent(OrderCreated orderCreated) {
        return OrderDispatched.builder()
                .orderId(orderCreated.getOrderId())
                .processedById(APPLICATION_ID)
                .notes("Dispatched: " + orderCreated.getItem())
                .build();
    }

    private void sendOrderDispatchedEvent(String key, OrderDispatched orderDispatched) throws InterruptedException, ExecutionException {
        kafkaTemplate.send(ORDER_DISPATCHED_TOPIC, key, orderDispatched).get();
    }

    private DispatchPreparing getDispatchPreparingEvent(OrderCreated orderCreated) {
        return DispatchPreparing.builder()
                .orderId(orderCreated.getOrderId())
                .build();
    }

    private void sendDispatchPreparingEvent(String key, DispatchPreparing dispatchPreparingEvent) throws InterruptedException, ExecutionException {
        kafkaTemplate.send(DISPATCH_TRACKING_TOPIC, key, dispatchPreparingEvent).get();
    }
}
