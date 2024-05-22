package com.example.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.Final.OrderRepo;
import com.example.Final.OrderTable;

import io.jsonwebtoken.Claims;


@Service
public class GetActiveOrdersCommand implements Command {
    private JwtDecoderService jwtDecoderService;
    
    private OrderRepo orderRepo;
    
    @Autowired
    public GetActiveOrdersCommand(OrderRepo orderRepo,JwtDecoderService jwtDecoderService) {
    	this.orderRepo=orderRepo;
    	this.jwtDecoderService=jwtDecoderService;
    }

    @Override
    public Object execute(Map<String, Object> data) {
        String userId=(String)data.get("userId");
            if(userId !=null){
            
            return orderRepo.getActiveOrders(UUID.fromString(userId));	
            }else {
            	return new ArrayList<OrderTable>();
            }
    }


}
