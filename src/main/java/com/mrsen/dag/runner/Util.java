package com.mrsen.dag.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Util {
    private static ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> getMapFromJson(String jsonPath) {
        try {
            return mapper.readValue(jsonPath, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
