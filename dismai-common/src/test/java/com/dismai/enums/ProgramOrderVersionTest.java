package com.dismai.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProgramOrderVersionTest {

    @Test
    void shouldResolveSupportedOrderVersion() {
        assertEquals(ProgramOrderVersion.V4_VERSION, ProgramOrderVersion.getRc("v4"));
        assertEquals("v4版本", ProgramOrderVersion.getMsg("v4"));
    }

    @Test
    void unknownVersionShouldReturnEmptyMessageAndNullEnum() {
        assertEquals("", ProgramOrderVersion.getMsg("unknown"));
        assertNull(ProgramOrderVersion.getRc("unknown"));
    }
}
