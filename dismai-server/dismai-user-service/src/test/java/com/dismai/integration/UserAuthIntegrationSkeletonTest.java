package com.dismai.integration;

import com.dismai.UserApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
@Disabled("Integration skeleton only. Requires user service, Redis and database fixtures before execution.")
@SpringBootTest(classes = UserApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAuthIntegrationSkeletonTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldLoginAndWriteSecurityArtifacts() {
        // Execution step placeholder: POST /user/login with a registered account fixture.
        // Execution step placeholder: Verify token/cookie persistence and authenticated access to a backend endpoint.
        assertNotNull(restTemplate);
    }
}
