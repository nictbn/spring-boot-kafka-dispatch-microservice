package com.example.dispatch.service;

import com.example.dispatch.message.DispatchPreparing;
import com.example.dispatch.message.OrderCreated;
import com.example.dispatch.message.OrderDispatched;
import com.example.dispatch.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static com.example.dispatch.service.DispatchService.DISPATCH_TRACKING_TOPIC;
import static com.example.dispatch.service.DispatchService.ORDER_DISPATCHED_TOPIC;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DispatchServiceTest {
    private DispatchService service;
    private KafkaTemplate kafkaProducerMock;

    @BeforeEach
    void setUp() {
        kafkaProducerMock = mock(KafkaTemplate.class);
        service = new DispatchService(kafkaProducerMock);
    }

    @Test
    void processSuccess() throws Exception {
        String key = randomUUID().toString();
        when(kafkaProducerMock.send(anyString(), anyString(), any(OrderDispatched.class))).thenReturn(mock(CompletableFuture.class));
        when(kafkaProducerMock.send(anyString(), anyString(), any(DispatchPreparing.class))).thenReturn(mock(CompletableFuture.class));
        OrderCreated testEvent = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        service.process(key, testEvent);
        verify(kafkaProducerMock, times(1)).send(eq(ORDER_DISPATCHED_TOPIC), eq(key), any(OrderDispatched.class));
        verify(kafkaProducerMock, times(1)).send(eq(DISPATCH_TRACKING_TOPIC), eq(key), any(DispatchPreparing.class));
    }

    @Test
    void processFailure() throws Exception {
        String key = randomUUID().toString();
        OrderCreated testEvent = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        doThrow(new RuntimeException("Producer failure")).when(kafkaProducerMock).send(eq(ORDER_DISPATCHED_TOPIC), eq(key), any(OrderDispatched.class));
        Exception exception = assertThrows(RuntimeException.class, () -> service.process(key, testEvent));
        verify(kafkaProducerMock, times(1)).send(eq(ORDER_DISPATCHED_TOPIC), eq(key), any(OrderDispatched.class));
        assertThat(exception.getMessage(), equalTo("Producer failure"));
    }
}