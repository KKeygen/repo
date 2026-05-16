package com.dismai.captcha.model.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class PointVO {
    private String secretKey;

    public int x;

    public int y;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public PointVO(int x, int y, String secretKey) {
        this.secretKey = secretKey;
        this.x = x;
        this.y = y;
    }

    public PointVO() {
    }

    public PointVO(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toJsonString() {
        return String.format("{\"secretKey\":\"%s\",\"x\":%d,\"y\":%d}", secretKey, x, y);
    }

    public PointVO parse(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return this;
        }
        try {
            JSONObject obj = JSON.parseObject(jsonStr);
            if (obj != null) {
                setX(obj.getIntValue("x"));
                setY(obj.getIntValue("y"));
                setSecretKey(obj.getString("secretKey"));
                return this;
            }
        } catch (Exception ignored) {
        }

        // Fallback: tolerant parse of simple key:value pairs
        try {
            Map<String, Object> m = new HashMap<>(4);
            String s = jsonStr.replaceFirst(",\\{", "\\{")
                    .replaceFirst("\\{", "")
                    .replaceFirst("\\}", "")
                    .replaceAll("\"", "");
            String[] parts = s.split(",");
            for (String item : parts) {
                String[] kv = item.split(":", 2);
                if (kv.length >= 2) {
                    m.put(kv[0], kv[1]);
                }
            }
            setX(Double.valueOf(String.valueOf(m.getOrDefault("x", "0"))).intValue());
            setY(Double.valueOf(String.valueOf(m.getOrDefault("y", "0"))).intValue());
            setSecretKey(String.valueOf(m.getOrDefault("secretKey", "")));
        } catch (Exception ignored) {
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointVO pointVO = (PointVO) o;
        return x == pointVO.x && y == pointVO.y && Objects.equals(secretKey, pointVO.secretKey);
    }

    @Override
    public int hashCode() {

        return Objects.hash(secretKey, x, y);
    }
}
