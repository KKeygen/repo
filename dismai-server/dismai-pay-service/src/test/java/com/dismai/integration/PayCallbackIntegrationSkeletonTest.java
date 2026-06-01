package com.dismai.integration;

import com.dismai.PayApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
@Disabled("Integration skeleton only. Requires pay and order service fixtures before execution.")
@SpringBootTest(classes = PayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PayCallbackIntegrationSkeletonTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldUpdateOrderAfterPayNotify() {
        // Execution step placeholder: POST /pay/notify or the corresponding callback entry with a signed payload fixture.
        // Execution step placeholder: Verify the order status is updated and the downstream seat state changes accordingly.
        assertNotNull(restTemplate);
    }
}
