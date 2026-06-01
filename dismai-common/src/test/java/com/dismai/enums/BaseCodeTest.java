package com.dismai.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BaseCodeTest {

    @Test
    void shouldResolveKnownBusinessCode() {
        assertEquals(BaseCode.SEAT_LOCK, BaseCode.getRc(40002));
        assertEquals("座位已锁定", BaseCode.getMsg(40002));
    }

    @Test
    void unknownCodeShouldReturnEmptyMessageAndNullEnum() {
        assertEquals("", BaseCode.getMsg(-999999));
        assertNull(BaseCode.getRc(-999999));
    }
}
