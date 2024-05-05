package com.example.Kafka;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerResponses {

	@KafkaListener(topics="cartResponses2",groupId = "KafkaGroupResponse")
	public void consumeMessage(String message) {
		System.out.println("Response1: "+message);
	}

}
