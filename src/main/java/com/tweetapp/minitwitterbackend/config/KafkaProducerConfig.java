// package com.tweetapp.minitwitterbackend.config;

// import com.tweetapp.minitwitterbackend.utility.ApplicationConstants;

// import org.apache.kafka.clients.admin.NewTopic;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Service;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Configuration
// @Service
// public class KafkaProducerConfig {

// 	@Autowired
// 	private KafkaTemplate<String, String> kafkaTemplate;

// 	public void sendMessage(String message) {
// 		log.info("Message sent by producer-> " +message);
// 		this.kafkaTemplate.send(ApplicationConstants.TOPIC_NAME, ApplicationConstants.TOPIC_NAME, message);
// 	}

// 	@Bean
// 	public NewTopic createTopic() {
// 		return new NewTopic(ApplicationConstants.TOPIC_NAME, 3, (short) 1);
// 	}

// }
