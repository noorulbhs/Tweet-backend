package com.tweetapp.minitwitterbackend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.minitwitterbackend.dto.Tweet;

@Repository
public interface TweetRepo extends MongoRepository<Tweet, Integer> {

	@Query("{ userName : ?0}")
	List<Tweet> findByUserName(String userName);

}
