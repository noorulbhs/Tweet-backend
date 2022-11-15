package com.tweetapp.minitwitterbackend.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.minitwitterbackend.dto.User;

@Repository
public interface UserRepo extends MongoRepository<User, Long> {
	@Query("{ emailId : ?0,password: ?1 }")
	Optional<User> findByEmailIdAndPassword(String emailId, String password);

	@Query("{ emailId : ?0}")
	Optional<User> findByEmailId(String userName);

}