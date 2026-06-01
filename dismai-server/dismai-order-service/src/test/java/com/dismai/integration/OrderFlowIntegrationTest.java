package com.dismai.integration;

import com.dismai.OrderApplication;
import com.dismai.client.UserClient;
import com.dismai.common.ApiResponse;
import com.dismai.dto.OrderCreateDto;
import com.dismai.dto.OrderTicketUserCreateDto;
import com.dismai.mapper.OrderMapper;
import com.dismai.mapper.OrderTicketUserMapper;
import com.dismai.redis.RedisCache;
import com.dismai.vo.TicketUserVo;
import com.dismai.vo.UserGetAndTicketUserListVo;
import com.dismai.vo.UserVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Tag("integration")
@EnabledIfEnvironmentVariable(named = "RUN_INTEGRATION_TESTS", matches = "true")
@SpringBootTest(classes = OrderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderFlowIntegrationTest {

    private static final Long ORDER_NUMBER = 910000000000101L;
    private static final Long USER_ID = 910000000000201L;
    private static final Long PROGRAM_ID = 2181859535445270528L;
    private static final Long FIRST_TICKET_USER_ID = 910000000000301L;
    private static final Long SECOND_TICKET_USER_ID = 910000000000302L;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderTicketUserMapper orderTicketUserMapper;

    @MockBean
    private RedisCache redisCache;

    @MockBean
    private UserClient userClient;

    private OrderCreateDto orderCreateDto;

    @BeforeEach
    void setUp() {
        orderCreateDto = new OrderCreateDto();
        orderCreateDto.setOrderNumber(ORDER_NUMBER);
        orderCreateDto.setProgramId(PROGRAM_ID);
        orderCreateDto.setProgramItemPicture("https://example.com/program-item.jpg");
        orderCreateDto.setUserId(USER_ID);
        orderCreateDto.setProgramTitle("Integration Concert");
        orderCreateDto.setProgramPlace("Hall A");
        orderCreateDto.setProgramShowTime(java.util.Date.from(Instant.parse("2025-02-01T10:00:00Z")));
        orderCreateDto.setProgramPermitChooseSeat(0);
        orderCreateDto.setDistributionMode("电子票");
        orderCreateDto.setTakeTicketMode("身份证");
        orderCreateDto.setOrderPrice(new BigDecimal("199.00"));
        orderCreateDto.setCreateOrderTime(java.util.Date.from(Instant.parse("2025-01-01T10:00:00Z")));
        orderCreateDto.setOrderTicketUserCreateDtoList(List.of(
                ticketUserCreateDto(FIRST_TICKET_USER_ID, "A区1排1座", new BigDecimal("99.50")),
                ticketUserCreateDto(SECOND_TICKET_USER_ID, "A区1排2座", new BigDecimal("99.50"))
        ));

        when(redisCache.incrBy(any(), anyLong())).thenReturn(2L);
        when(userClient.getUserAndTicketUserList(any())).thenReturn(ApiResponse.ok(buildUserFixture()));
    }

    @AfterEach
    void tearDown() {
        orderTicketUserMapper.delete(Wrappers.lambdaQuery(com.dismai.entity.OrderTicketUser.class)
                .eq(com.dismai.entity.OrderTicketUser::getOrderNumber, ORDER_NUMBER));
        orderMapper.delete(Wrappers.lambdaQuery(com.dismai.entity.Order.class)
                .eq(com.dismai.entity.Order::getOrderNumber, ORDER_NUMBER));
    }

    @Test
    void shouldCreateOrderReturnOrderDetailAndRejectDuplicateOrderNumber() throws Exception {
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                "/order/create",
                new HttpEntity<>(orderCreateDto, jsonHeaders()),
                String.class);

        assertEquals(200, createResponse.getStatusCode().value());
        JsonNode createBody = objectMapper.readTree(createResponse.getBody());
        assertEquals(0, createBody.path("code").asInt());
        assertEquals(String.valueOf(ORDER_NUMBER), createBody.path("data").asText());

        ResponseEntity<String> getResponse = restTemplate.postForEntity(
                "/order/get",
                new HttpEntity<>(Map.of("orderNumber", ORDER_NUMBER), jsonHeaders()),
                String.class);

        assertEquals(200, getResponse.getStatusCode().value());
        JsonNode getBody = objectMapper.readTree(getResponse.getBody());
        assertEquals(0, getBody.path("code").asInt());
        assertEquals(ORDER_NUMBER, getBody.path("data").path("orderNumber").asLong());
        assertEquals(USER_ID, getBody.path("data").path("userId").asLong());
        assertTrue(getBody.path("data").path("orderTicketInfoVoList").size() >= 1);
        assertEquals(2, getBody.path("data").path("orderTicketInfoVoList").path(0).path("quantity").asInt());
        assertEquals(USER_ID, getBody.path("data").path("userAndTicketUserInfoVo").path("userInfoVo").path("id").asLong());

        ResponseEntity<String> listResponse = restTemplate.postForEntity(
                "/order/select/list",
                new HttpEntity<>(Map.of("userId", USER_ID), jsonHeaders()),
                String.class);

        assertEquals(200, listResponse.getStatusCode().value());
        JsonNode listBody = objectMapper.readTree(listResponse.getBody());
        assertEquals(0, listBody.path("code").asInt());
        assertTrue(listBody.path("data").size() >= 1);
        assertEquals(2, listBody.path("data").path(0).path("ticketCount").asInt());

        ResponseEntity<String> duplicateResponse = restTemplate.postForEntity(
                "/order/create",
                new HttpEntity<>(orderCreateDto, jsonHeaders()),
                String.class);

        assertEquals(200, duplicateResponse.getStatusCode().value());
        JsonNode duplicateBody = objectMapper.readTree(duplicateResponse.getBody());
        assertEquals(40025, duplicateBody.path("code").asInt());
        assertFalse(duplicateBody.path("message").asText().isBlank());
    }

    private OrderTicketUserCreateDto ticketUserCreateDto(Long ticketUserId, String seatInfo, BigDecimal price) {
        OrderTicketUserCreateDto dto = new OrderTicketUserCreateDto();
        dto.setOrderNumber(ORDER_NUMBER);
        dto.setProgramId(PROGRAM_ID);
        dto.setUserId(USER_ID);
        dto.setTicketUserId(ticketUserId);
        dto.setSeatId(ticketUserId);
        dto.setSeatInfo(seatInfo);
        dto.setTicketCategoryId(700000000000001L);
        dto.setOrderPrice(price);
        dto.setCreateOrderTime(java.util.Date.from(Instant.parse("2025-01-01T10:00:00Z")));
        return dto;
    }

    private UserGetAndTicketUserListVo buildUserFixture() {
        UserVo userVo = new UserVo();
        userVo.setId(USER_ID);
        userVo.setName("integration-order-user");
        userVo.setRelName("integration order user");
        userVo.setMobile("13800138002");

        TicketUserVo firstTicketUser = new TicketUserVo();
        firstTicketUser.setId(FIRST_TICKET_USER_ID);
        firstTicketUser.setUserId(USER_ID);
        firstTicketUser.setRelName("Alice");
        firstTicketUser.setIdNumber("110101199001011234");

        TicketUserVo secondTicketUser = new TicketUserVo();
        secondTicketUser.setId(SECOND_TICKET_USER_ID);
        secondTicketUser.setUserId(USER_ID);
        secondTicketUser.setRelName("Bob");
        secondTicketUser.setIdNumber("110101199201019876");

        UserGetAndTicketUserListVo vo = new UserGetAndTicketUserListVo();
        vo.setUserVo(userVo);
        vo.setTicketUserVoList(List.of(firstTicketUser, secondTicketUser));
        return vo;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
