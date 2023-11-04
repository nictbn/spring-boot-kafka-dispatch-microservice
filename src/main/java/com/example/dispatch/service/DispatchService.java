package com.example.dispatch.service;

import com.example.dispatch.message.OrderCreated;
import com.example.dispatch.message.OrderDispatched;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DispatchService {
    public static final String ORDER_DISPATCHED_TOPIC = "order.dispatched";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void process(OrderCreated orderCreated) throws Exception {
        OrderDispatched orderDispatched = getOrderDispatchedEvent(orderCreated);
        sendOrderDispatchedEvent(orderDispatched);
    }

    private static OrderDispatched getOrderDispatchedEvent(OrderCreated orderCreated) {
        return OrderDispatched.builder()
                .orderId(orderCreated.getOrderId())
                .build();
    }

    private void sendOrderDispatchedEvent(OrderDispatched orderDispatched) throws InterruptedException, ExecutionException {
        kafkaTemplate.send(ORDER_DISPATCHED_TOPIC, orderDispatched).get();
    }
}
