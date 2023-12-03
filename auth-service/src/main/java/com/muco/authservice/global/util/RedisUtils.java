package com.muco.authservice.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisUtils implements InitializingBean {

    private final RedisTemplate<String, Object> template;
    private static ValueOperations<String, Object> ops;

    @Override
    public void afterPropertiesSet() throws Exception {
        ops = template.opsForValue();
    }

    public static void saveValue(String key, String value) {
        ops.set(key, value);
    }

    public static void saveValue(String key, String value, Duration duration) {
        ops.set(key, value, duration);
    }

    public static void saveData(String key, Object value) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(value);
        ops.set(key, json);
    }

    public static Optional<String> getValue(String key) {
        return Optional.ofNullable((String) ops.get(key));
    }

    public static <T> T getData(String key, Class<T> clazz) throws JsonProcessingException {
        String json = (String) ops.get(key);
        if (StringUtils.hasText(json)) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, clazz);
        } else {
            return null;
        }
    }

    public static void deleteValue(String key) {
        ops.getAndDelete(key);
    }
}