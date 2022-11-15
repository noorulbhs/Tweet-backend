// package com.tweetapp.minitwitterbackend.config;

// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;

// import com.tweetapp.minitwitterbackend.utility.ApplicationConstants;

// import lombok.Generated;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Generated
// @Service
// public class KafkaConsumerConfig {

// 	@KafkaListener(topics = "message", groupId = ApplicationConstants.GROUP_ID)
// 	public void consume(String message) {
// 		//System.out.println("message received" + message);
// 		log.info("Message received by consumer-> " +message);
// 	}
// }
