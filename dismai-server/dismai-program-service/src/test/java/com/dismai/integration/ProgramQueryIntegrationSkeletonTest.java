package com.dismai.integration;

import com.dismai.ProgramApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
@Disabled("Integration skeleton only. Requires program service, Elasticsearch and database fixtures before execution.")
@SpringBootTest(classes = ProgramApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProgramQueryIntegrationSkeletonTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnProgramListDetailAndSeatData() {
        // Execution step placeholder: Call the program list/detail endpoints through the service entry or gateway.
        // Execution step placeholder: Verify list, detail, ticket category and seat payloads match the seeded fixtures.
        assertNotNull(restTemplate);
    }
}
