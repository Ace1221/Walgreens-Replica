package com.example.Commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.common.Uuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Cache.SessionCache;
import com.example.Final.CartRepo;
import com.example.Final.CartTable;
import com.example.Final.PromoRepo;
import com.example.Final.UserUsedPromoRepo;
import com.example.Kafka.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
@Service
public class ProceedToCheckOutCommand implements Command {

    private JwtDecoderService jwtDecoderService;
    
    private CartRepo cartRepo;
    
    private KafkaProducer kafkaProducer;

    @Autowired
	private SessionCache sessionCache;
    
    @Autowired
    public ProceedToCheckOutCommand(CartRepo cartRepo, JwtDecoderService jwtDecoderService, PromoRepo promoRepo, UserUsedPromoRepo userUsedPromoRepo,KafkaProducer kafkaProducer, SessionCache sessionCache) {
    	this.cartRepo=cartRepo;
    	this.jwtDecoderService=jwtDecoderService;
        this.kafkaProducer = kafkaProducer;
        this.sessionCache = sessionCache;
    }

    @Override
    public Object execute(Map<String, Object> data) throws Exception {
        
        String user = (String)data.get("userId");
        CartTable userCart = cartRepo.getCart(UUID.fromString(user));
        Map<String, Object> request = new HashMap<>();
        request.put("commandName", "Checkout");
        request.put("data", userCart);
        ObjectMapper objectMapper = new ObjectMapper();
        String userCartString = objectMapper.writeValueAsString(request);

        //TODO: Publish to Payment Service Kafka
        //kafkaProducer.publishToTopic("paymentRequests", userCartString);
        //or Call API to payment service
        return "Checkout Request Sent";
        
    }


}
