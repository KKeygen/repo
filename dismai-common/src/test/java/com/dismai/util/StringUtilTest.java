package com.dismai.util;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilTest {

    @Test
    void isEmptyShouldTreatBlankNullTextAndUndefinedAsEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty("   "));
        assertTrue(StringUtil.isEmpty("null"));
        assertTrue(StringUtil.isEmpty("undefined"));
        assertFalse(StringUtil.isEmpty("13800138000"));
    }

    @Test
    void shouldConvertInputStreamToString() {
        ByteArrayInputStream stream = new ByteArrayInputStream("seat-lock".getBytes(StandardCharsets.UTF_8));

        assertEquals("seat-lock", StringUtil.inputStreamConvertString(stream));
        assertNull(StringUtil.inputStreamConvertString(null));
    }

    @Test
    void shouldDecodeQueryStringToMap() {
        Map<String, String> result = StringUtil.convertQueryStringToMap("mobile=13800138000&city=%E4%B8%8A%E6%B5%B7&empty=");

        assertEquals("13800138000", result.get("mobile"));
        assertEquals("上海", result.get("city"));
        assertFalse(result.containsKey("empty"));
    }
}
