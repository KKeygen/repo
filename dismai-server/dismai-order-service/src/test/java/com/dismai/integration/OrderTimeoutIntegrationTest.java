package com.dismai.integration;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dismai.OrderApplication;
import com.dismai.client.UserClient;
import com.dismai.common.ApiResponse;
import com.dismai.dto.OrderCreateDto;
import com.dismai.dto.OrderTicketUserCreateDto;
import com.dismai.entity.Order;
import com.dismai.entity.OrderTicketUser;
import com.dismai.mapper.OrderMapper;
import com.dismai.mapper.OrderTicketUserMapper;
import com.dismai.redis.RedisCache;
import com.dismai.service.lua.OrderProgramCacheResolutionOperate;
import com.dismai.servicelock.LockType;
import com.dismai.util.ServiceLockTool;
import com.dismai.vo.SeatVo;
import com.dismai.vo.TicketUserVo;
import com.dismai.vo.UserGetAndTicketUserListVo;
import com.dismai.vo.UserVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.redisson.api.RLock;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("integration")
@EnabledIfEnvironmentVariable(named = "RUN_INTEGRATION_TESTS", matches = "true")
@SpringBootTest(classes = OrderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderTimeoutIntegrationTest {

    private static final Long ORDER_NUMBER = 910000000000102L;
    private static final Long USER_ID = 910000000000202L;
    private static final Long PROGRAM_ID = 2181859535445270528L;
    private static final Long TICKET_USER_ID = 910000000000303L;
    private static final Long SEAT_ID = 910000000000401L;
    private static final Long TICKET_CATEGORY_ID = 700000000000002L;

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

    @MockBean
    private ServiceLockTool serviceLockTool;

    @MockBean
    private OrderProgramCacheResolutionOperate orderProgramCacheResolutionOperate;

    private OrderCreateDto orderCreateDto;

    @BeforeEach
    void setUp() {
        RLock lock = mock(RLock.class);
        doNothing().when(lock).lock();
        doNothing().when(lock).unlock();
        when(serviceLockTool.getLock(eq(LockType.Reentrant), anyString(), any(String[].class))).thenReturn(lock);
        when(redisCache.multiGetForHash(any(), anyList(), eq(SeatVo.class))).thenReturn(List.of(buildSeatVo()));
        doNothing().when(orderProgramCacheResolutionOperate).programCacheReverseOperate(anyList(), any(String[].class));

        orderCreateDto = new OrderCreateDto();
        orderCreateDto.setOrderNumber(ORDER_NUMBER);
        orderCreateDto.setProgramId(PROGRAM_ID);
        orderCreateDto.setProgramItemPicture("https://example.com/program-item.jpg");
        orderCreateDto.setUserId(USER_ID);
        orderCreateDto.setProgramTitle("Integration Timeout Concert");
        orderCreateDto.setProgramPlace("Hall B");
        orderCreateDto.setProgramShowTime(java.util.Date.from(Instant.parse("2025-02-01T10:00:00Z")));
        orderCreateDto.setProgramPermitChooseSeat(1);
        orderCreateDto.setDistributionMode("电子票");
        orderCreateDto.setTakeTicketMode("身份证");
        orderCreateDto.setOrderPrice(new BigDecimal("288.00"));
        orderCreateDto.setCreateOrderTime(java.util.Date.from(Instant.parse("2025-01-01T10:00:00Z")));
        orderCreateDto.setOrderTicketUserCreateDtoList(List.of(orderTicketUserCreateDto()));
        when(redisCache.incrBy(any(), org.mockito.ArgumentMatchers.anyLong())).thenReturn(1L);
        when(userClient.getUserAndTicketUserList(any())).thenReturn(ApiResponse.ok(buildUserFixture()));
    }

    @AfterEach
    void tearDown() {
        orderTicketUserMapper.delete(Wrappers.lambdaQuery(OrderTicketUser.class)
                .eq(OrderTicketUser::getOrderNumber, ORDER_NUMBER));
        orderMapper.delete(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getOrderNumber, ORDER_NUMBER));
    }

    @Test
    void shouldCancelUnpaidOrderAndUpdateRelatedState() throws Exception {
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                "/order/create",
                new HttpEntity<>(orderCreateDto, jsonHeaders()),
                String.class);
        assertEquals(200, createResponse.getStatusCode().value());
        assertEquals(0, objectMapper.readTree(createResponse.getBody()).path("code").asInt());

        ResponseEntity<String> cancelResponse = restTemplate.postForEntity(
                "/order/cancel",
                new HttpEntity<>(Map.of("orderNumber", ORDER_NUMBER), jsonHeaders()),
                String.class);
        assertEquals(200, cancelResponse.getStatusCode().value());
        JsonNode cancelBody = objectMapper.readTree(cancelResponse.getBody());
        assertEquals(0, cancelBody.path("code").asInt());
        assertTrue(cancelBody.path("data").asBoolean());

        ResponseEntity<String> getResponse = restTemplate.postForEntity(
                "/order/get",
                new HttpEntity<>(Map.of("orderNumber", ORDER_NUMBER), jsonHeaders()),
                String.class);
        assertEquals(200, getResponse.getStatusCode().value());
        JsonNode getBody = objectMapper.readTree(getResponse.getBody());
        assertEquals(0, getBody.path("code").asInt());
        assertEquals(2, getBody.path("data").path("orderStatus").asInt());
        assertFalse(getBody.path("data").path("cancelOrderTime").isNull());

        Order order = orderMapper.selectOne(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getOrderNumber, ORDER_NUMBER));
        assertEquals(2, order.getOrderStatus());
        List<OrderTicketUser> orderTicketUsers = orderTicketUserMapper.selectList(Wrappers.lambdaQuery(OrderTicketUser.class)
                .eq(OrderTicketUser::getOrderNumber, ORDER_NUMBER));
        assertEquals(1, orderTicketUsers.size());
        assertEquals(2, orderTicketUsers.get(0).getOrderStatus());
    }

    private OrderTicketUserCreateDto orderTicketUserCreateDto() {
        OrderTicketUserCreateDto dto = new OrderTicketUserCreateDto();
        dto.setOrderNumber(ORDER_NUMBER);
        dto.setProgramId(PROGRAM_ID);
        dto.setUserId(USER_ID);
        dto.setTicketUserId(TICKET_USER_ID);
        dto.setSeatId(SEAT_ID);
        dto.setSeatInfo("B区2排3座");
        dto.setTicketCategoryId(TICKET_CATEGORY_ID);
        dto.setOrderPrice(new BigDecimal("288.00"));
        dto.setCreateOrderTime(java.util.Date.from(Instant.parse("2025-01-01T10:00:00Z")));
        return dto;
    }

    private SeatVo buildSeatVo() {
        SeatVo seatVo = new SeatVo();
        seatVo.setId(SEAT_ID);
        seatVo.setProgramId(PROGRAM_ID);
        seatVo.setTicketCategoryId(TICKET_CATEGORY_ID);
        seatVo.setRowCode(2);
        seatVo.setColCode(3);
        seatVo.setSeatType(1);
        seatVo.setPrice(new BigDecimal("288.00"));
        seatVo.setSellStatus(2);
        return seatVo;
    }

    private UserGetAndTicketUserListVo buildUserFixture() {
        UserVo userVo = new UserVo();
        userVo.setId(USER_ID);
        userVo.setName("integration-timeout-user");
        userVo.setRelName("integration timeout user");

        TicketUserVo ticketUserVo = new TicketUserVo();
        ticketUserVo.setId(TICKET_USER_ID);
        ticketUserVo.setUserId(USER_ID);
        ticketUserVo.setRelName("Charlie");
        ticketUserVo.setIdNumber("110101199305051234");

        UserGetAndTicketUserListVo vo = new UserGetAndTicketUserListVo();
        vo.setUserVo(userVo);
        vo.setTicketUserVoList(List.of(ticketUserVo));
        return vo;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
