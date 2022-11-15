package com.tweetapp.minitwitterbackend.service;

import java.util.List;

import com.tweetapp.minitwitterbackend.dto.GenericResponse;
import com.tweetapp.minitwitterbackend.dto.Tweet;
import com.tweetapp.minitwitterbackend.exception.PersistenceException;
import com.tweetapp.minitwitterbackend.request.TweetRequest;

import org.springframework.http.ResponseEntity;

public interface TweetService {
    ResponseEntity<List<Tweet>> getAllTweets() throws PersistenceException;

    ResponseEntity<List<Tweet>> getAllTweetsByUser(String userName) throws PersistenceException;

    ResponseEntity<GenericResponse> postTweet(String userName, TweetRequest request) throws PersistenceException;

    ResponseEntity<String> updateTweet(String userName, int tweetId, TweetRequest request) throws PersistenceException;

    ResponseEntity<String> likeTweet(String userName, int tweetId) throws PersistenceException;

    ResponseEntity<GenericResponse> deleteTweet(String userName, int tweetId) throws PersistenceException;

    ResponseEntity<String> reply(String userName, int tweetId, String reply) throws PersistenceException;

}
