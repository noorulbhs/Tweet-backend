package com.tweetapp.minitwitterbackend.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.minitwitterbackend.dto.GenericResponse;
import com.tweetapp.minitwitterbackend.dto.Tweet;
import com.tweetapp.minitwitterbackend.exception.PersistenceException;
import com.tweetapp.minitwitterbackend.request.TweetRequest;
import com.tweetapp.minitwitterbackend.service.TweetService;

import lombok.Generated;

@RequestMapping(value = "/api/v1.0/tweets")
@RestController
@Generated
//@CrossOrigin(origins = "https://mini-twitter-frontend.azurewebsites.net")
@CrossOrigin(origins = "http://localhost:4200")
public class TweetController {

	@Autowired
	TweetService tweetService;

	@PostMapping("/add/{userName}")
	public ResponseEntity<GenericResponse> postTweet(@PathVariable("userName") String userName,
			@RequestBody @Valid TweetRequest tweet) throws PersistenceException {
		return tweetService.postTweet(userName, tweet);
	}

	@GetMapping("/all")
	public ResponseEntity<List<Tweet>> getAllTweet() throws PersistenceException {
		return tweetService.getAllTweets();
	}

	@GetMapping("/{userName}")
	public ResponseEntity<List<Tweet>> getAllUserTweet(@PathVariable String userName) throws PersistenceException {
		return tweetService.getAllTweetsByUser(userName);
	}

	@PutMapping("/{userName}/update/{tweetId}")
	public ResponseEntity<String> updateTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId, @RequestBody @Valid TweetRequest tweetRequest) throws PersistenceException {
		return tweetService.updateTweet(userName, tweetId, tweetRequest);
	}

	@DeleteMapping("/{userName}/delete/{tweetId}")
	public ResponseEntity<GenericResponse> deleteTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId) throws PersistenceException {
		return tweetService.deleteTweet(userName, tweetId);
	}

	@PutMapping("/{userName}/like/{tweetId}")
	public ResponseEntity<String> likeTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId) throws PersistenceException {
		return tweetService.likeTweet(userName, tweetId);
	}

	@PostMapping("/{userName}/reply/{tweetId}")
	public ResponseEntity<String> replyTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId, @RequestBody @Valid String reply) throws PersistenceException {
		return tweetService.reply(userName, tweetId, reply);
	}

}

