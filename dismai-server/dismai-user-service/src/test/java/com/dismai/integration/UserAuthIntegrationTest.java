package com.dismai.integration;

import com.dismai.UserApplication;
import com.dismai.client.BaseDataClient;
import com.dismai.common.ApiResponse;
import com.dismai.dto.GetChannelDataByCodeDto;
import com.dismai.entity.User;
import com.dismai.entity.UserMobile;
import com.dismai.mapper.UserMapper;
import com.dismai.mapper.UserMobileMapper;
import com.dismai.redis.RedisCache;
import com.dismai.vo.GetChannelDataVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("integration")
@EnabledIfEnvironmentVariable(named = "RUN_INTEGRATION_TESTS", matches = "true")
@SpringBootTest(classes = UserApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAuthIntegrationTest {

    private static final String CHANNEL_CODE = "0001";
    private static final String MOBILE = "13800138001";
    private static final String PASSWORD = "TestPwd123";
    private static final Long USER_ID = 910000000000001L;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMobileMapper userMobileMapper;

    @MockBean
    private BaseDataClient baseDataClient;

    @MockBean
    private RedisCache redisCache;

    private User insertedUser;
    private UserMobile insertedUserMobile;

    @BeforeEach
    void setUp() {
        insertedUser = new User();
        insertedUser.setId(USER_ID);
        insertedUser.setName("integration-user");
        insertedUser.setRelName("integration user");
        insertedUser.setMobile(MOBILE);
        insertedUser.setPassword(PASSWORD);
        insertedUser.setEmailStatus(0);
        insertedUser.setRelAuthenticationStatus(0);
        insertedUser.setStatus(1);
        insertedUser.setCreateTime(java.util.Date.from(Instant.parse("2025-01-01T00:00:00Z")));
        insertedUser.setEditTime(java.util.Date.from(Instant.parse("2025-01-01T00:00:00Z")));
        userMapper.insert(insertedUser);

        insertedUserMobile = new UserMobile();
        insertedUserMobile.setId(USER_ID + 1);
        insertedUserMobile.setUserId(USER_ID);
        insertedUserMobile.setMobile(MOBILE);
        insertedUserMobile.setStatus(1);
        insertedUserMobile.setCreateTime(java.util.Date.from(Instant.parse("2025-01-01T00:00:00Z")));
        insertedUserMobile.setEditTime(java.util.Date.from(Instant.parse("2025-01-01T00:00:00Z")));
        userMobileMapper.insert(insertedUserMobile);

        GetChannelDataVo channelDataVo = new GetChannelDataVo();
        channelDataVo.setCode(CHANNEL_CODE);
        channelDataVo.setTokenSecret("integration-token-secret");
        when(baseDataClient.getByCode(any(GetChannelDataByCodeDto.class))).thenReturn(ApiResponse.ok(channelDataVo));
    }

    @AfterEach
    void tearDown() {
        userMobileMapper.deleteById(insertedUserMobile.getId());
        userMapper.deleteById(insertedUser.getId());
    }

    @Test
    void shouldLoginAndLogoutWithIssuedToken() throws Exception {
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "/user/login",
                new HttpEntity<>(loginRequestBody(), jsonHeaders()),
                String.class);

        assertEquals(200, loginResponse.getStatusCode().value());
        JsonNode loginBody = objectMapper.readTree(loginResponse.getBody());
        assertEquals(0, loginBody.path("code").asInt());
        assertEquals(USER_ID, loginBody.path("data").path("userId").asLong());
        String token = loginBody.path("data").path("token").asText();
        assertFalse(token.isBlank());

        ResponseEntity<String> logoutResponse = restTemplate.postForEntity(
                "/user/logout",
                new HttpEntity<>(logoutRequestBody(token), jsonHeaders()),
                String.class);

        assertEquals(200, logoutResponse.getStatusCode().value());
        JsonNode logoutBody = objectMapper.readTree(logoutResponse.getBody());
        assertEquals(0, logoutBody.path("code").asInt());
        assertTrue(logoutBody.path("data").asBoolean());
    }

    private Map<String, Object> loginRequestBody() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", CHANNEL_CODE);
        body.put("mobile", MOBILE);
        body.put("password", PASSWORD);
        return body;
    }

    private Map<String, Object> logoutRequestBody(String token) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", CHANNEL_CODE);
        body.put("token", token);
        return body;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
