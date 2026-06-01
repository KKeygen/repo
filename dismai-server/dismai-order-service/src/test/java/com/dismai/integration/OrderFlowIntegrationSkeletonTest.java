package com.dismai.integration;

import com.dismai.OrderApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
@Disabled("Integration skeleton only. Requires Redis, Kafka and order service fixtures before execution.")
@SpringBootTest(classes = OrderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderFlowIntegrationSkeletonTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldLockSeatAndCreateOrderMessage() {
        // Execution step placeholder: POST /program/order/create/v4 with a seat fixture.
        // Execution step placeholder: Assert the same seat cannot be locked twice and the order message is emitted.
        assertNotNull(restTemplate);
    }
}
