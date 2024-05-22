package com.example.Kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.Final.CartTable;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class StringFormulator {
    Logger logger = LoggerFactory.getLogger(StringFormulator.class);

    public StringFormulator(){
        
    }

    public Object getData(String message){
        ObjectMapper objectMapper = new ObjectMapper();
        message=message.replace("\\", "");
        message=message.substring(1, message.length()-1);
        try{
            JsonNode jsonNode = objectMapper.readTree(message);
            Map<String, Object> map = objectMapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {});
            // Extract the "data" field and convert it into a CartTable object
            String res= (String)map.get("data");
            return res;
        }catch(Exception e){
            return "Invalid Data";
        }
    }


}
