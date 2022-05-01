package com.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommonConverterUtil {

    private final ObjectMapper objectMapper;

    public Object converter(Object o, Class clazz) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(o);
        return objectMapper.readValue(json, clazz);
    }
}
