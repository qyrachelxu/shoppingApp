package com.xiang.authentication.util;

import com.xiang.authentication.domain.message.EmailDetail;
import com.xiang.authentication.domain.message.SimpleMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializeUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(SimpleMessage message){

        String result = null;

        try {
            result = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String serialize(EmailDetail message){

        String result = null;

        try {
            result = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

}
