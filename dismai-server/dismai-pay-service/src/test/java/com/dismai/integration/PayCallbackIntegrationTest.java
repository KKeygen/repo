package com.dismai.integration;

import com.dismai.PayApplication;
import com.dismai.entity.PayBill;
import com.dismai.enums.PayBillStatus;
import com.dismai.mapper.PayBillMapper;
import com.dismai.pay.PayStrategyContext;
import com.dismai.pay.PayStrategyHandler;
import com.dismai.pay.TradeResult;
import com.dismai.servicelock.LockType;
import com.dismai.util.ServiceLockTool;
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
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("integration")
@EnabledIfEnvironmentVariable(named = "RUN_INTEGRATION_TESTS", matches = "true")
@SpringBootTest(classes = PayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PayCallbackIntegrationTest {

    private static final String CHANNEL = "alipay";
    private static final String OUT_TRADE_NO = "910000000000301";
    private static final BigDecimal PAY_AMOUNT = new BigDecimal("19.90");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PayBillMapper payBillMapper;

    @MockBean
    private PayStrategyContext payStrategyContext;

    @MockBean
    private ServiceLockTool serviceLockTool;

    private PayBill insertedPayBill;
    private PayStrategyHandler payStrategyHandler;

    @BeforeEach
    void setUp() {
        RLock lock = mock(RLock.class);
        doNothing().when(lock).lock();
        doNothing().when(lock).unlock();
        when(serviceLockTool.getLock(eq(LockType.Reentrant), anyString(), any(String[].class))).thenReturn(lock);

        payStrategyHandler = mock(PayStrategyHandler.class);
        when(payStrategyContext.get(CHANNEL)).thenReturn(payStrategyHandler);
        when(payStrategyHandler.signVerify(anyMap())).thenReturn(true);
        when(payStrategyHandler.dataVerify(anyMap(), any(PayBill.class))).thenReturn(true);

        insertedPayBill = new PayBill();
        insertedPayBill.setId(910000000000401L);
        insertedPayBill.setOutOrderNo(OUT_TRADE_NO);
        insertedPayBill.setPayChannel(CHANNEL);
        insertedPayBill.setPayScene("生产");
        insertedPayBill.setSubject("Integration Payment");
        insertedPayBill.setPayAmount(PAY_AMOUNT);
        insertedPayBill.setPayBillType(1);
        insertedPayBill.setPayBillStatus(PayBillStatus.NO_PAY.getCode());
        insertedPayBill.setPayTime(null);
        insertedPayBill.setStatus(1);
        insertedPayBill.setCreateTime(java.util.Date.from(Instant.parse("2025-01-01T00:00:00Z")));
        insertedPayBill.setEditTime(java.util.Date.from(Instant.parse("2025-01-01T00:00:00Z")));
        payBillMapper.insert(insertedPayBill);

        when(payStrategyHandler.queryTrade(OUT_TRADE_NO)).thenReturn(buildTradeResult());
    }

    @AfterEach
    void tearDown() {
        payBillMapper.deleteById(insertedPayBill.getId());
    }

    @Test
    void shouldAcceptCallbackAndRefreshTradeStatus() throws Exception {
        ResponseEntity<String> notifyResponse = restTemplate.postForEntity(
                "/pay/notify",
                new HttpEntity<>(buildNotifyRequest(), jsonHeaders()),
                String.class);

        assertEquals(200, notifyResponse.getStatusCode().value());
        JsonNode notifyBody = objectMapper.readTree(notifyResponse.getBody());
        assertEquals(0, notifyBody.path("code").asInt());
        assertEquals(OUT_TRADE_NO, notifyBody.path("data").path("outTradeNo").asText());
        assertEquals("success", notifyBody.path("data").path("payResult").asText());

        ResponseEntity<String> tradeCheckResponse = restTemplate.postForEntity(
                "/pay/trade/check",
                new HttpEntity<>(buildTradeCheckRequest(), jsonHeaders()),
                String.class);

        assertEquals(200, tradeCheckResponse.getStatusCode().value());
        JsonNode tradeCheckBody = objectMapper.readTree(tradeCheckResponse.getBody());
        assertEquals(0, tradeCheckBody.path("code").asInt());
        assertTrue(tradeCheckBody.path("data").path("success").asBoolean());
        assertEquals(OUT_TRADE_NO, tradeCheckBody.path("data").path("outTradeNo").asText());
        assertEquals(PayBillStatus.PAY.getCode(), tradeCheckBody.path("data").path("payBillStatus").asInt());
        assertEquals(PAY_AMOUNT, tradeCheckBody.path("data").path("totalAmount").decimalValue());

        PayBill updatedPayBill = payBillMapper.selectById(insertedPayBill.getId());
        assertEquals(PayBillStatus.PAY.getCode(), updatedPayBill.getPayBillStatus());
    }

    private Map<String, Object> buildNotifyRequest() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("out_trade_no", OUT_TRADE_NO);
        params.put("trade_status", "TRADE_SUCCESS");
        params.put("total_amount", PAY_AMOUNT.toPlainString());
        params.put("trade_no", "202501010001");
        params.put("buyer_pay_amount", PAY_AMOUNT.toPlainString());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("channel", CHANNEL);
        body.put("params", params);
        return body;
    }

    private Map<String, Object> buildTradeCheckRequest() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("outTradeNo", OUT_TRADE_NO);
        body.put("channel", CHANNEL);
        return body;
    }

    private TradeResult buildTradeResult() {
        TradeResult tradeResult = new TradeResult();
        tradeResult.setSuccess(true);
        tradeResult.setPayBillStatus(PayBillStatus.PAY.getCode());
        tradeResult.setOutTradeNo(OUT_TRADE_NO);
        tradeResult.setTotalAmount(PAY_AMOUNT);
        return tradeResult;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
