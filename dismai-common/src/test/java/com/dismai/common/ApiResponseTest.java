package com.dismai.common;

import com.dismai.enums.BaseCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApiResponseTest {

    @Test
    void okShouldCarrySuccessCodeAndPayload() {
        ApiResponse<String> response = ApiResponse.ok("demo");

        assertEquals(0, response.getCode());
        assertEquals("demo", response.getData());
        assertNull(response.getMessage());
    }

    @Test
    void errorShouldUseBaseCodeContract() {
        ApiResponse<String> response = ApiResponse.error(BaseCode.SEAT_LOCK, "A1-01");

        assertEquals(BaseCode.SEAT_LOCK.getCode(), response.getCode());
        assertEquals(BaseCode.SEAT_LOCK.getMsg(), response.getMessage());
        assertEquals("A1-01", response.getData());
    }

    @Test
    void defaultErrorShouldHaveStableFallbackMessage() {
        ApiResponse<Object> response = ApiResponse.error();

        assertEquals(-100, response.getCode());
        assertEquals("系统错误，请稍后重试!", response.getMessage());
        assertNull(response.getData());
    }
}
