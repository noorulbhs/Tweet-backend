package com.tweetapp.minitwitterbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class MiniTwitterBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniTwitterBackendApplication.class, args);
	}

}
