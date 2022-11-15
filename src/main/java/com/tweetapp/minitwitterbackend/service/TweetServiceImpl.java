package com.tweetapp.minitwitterbackend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tweetapp.minitwitterbackend.dto.GenericResponse;
//import com.tweetapp.minitwitterbackend.config.KafkaProducerConfig;
import com.tweetapp.minitwitterbackend.dto.Tweet;
import com.tweetapp.minitwitterbackend.dto.User;
import com.tweetapp.minitwitterbackend.exception.PersistenceException;
import com.tweetapp.minitwitterbackend.repository.TweetRepo;
import com.tweetapp.minitwitterbackend.repository.UserRepo;
import com.tweetapp.minitwitterbackend.request.TweetRequest;
import com.tweetapp.minitwitterbackend.utility.ApplicationConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetServiceImpl implements TweetService {
    @Autowired
    private TweetRepo tweetRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private MongoOperations mongoOperations;


    @Override
    public ResponseEntity<GenericResponse> deleteTweet(final String userName, final int tweetId) throws PersistenceException {
        log.info("Starting Delete tweet method");
        GenericResponse response = new GenericResponse();
        try {
            if (validUser(userName) && validTweet(tweetId)) {
                tweetRepository.deleteById(tweetId);
                //producer.sendMessage("Tweet Deleted with the following tweetId" + tweetId);
                log.info("Ending Delete tweet method");
                response.setGenericResponse(ApplicationConstants.TWEET_DELETED);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new PersistenceException(ApplicationConstants.FAILED, ApplicationConstants.TWEET_ERROR_ID);
        }
        log.info("Ending Delete tweet method");
        response.setGenericResponse(ApplicationConstants.FAILED);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Tweet>> getAllTweets() throws PersistenceException {
        log.info("Starting getAll tweet method");
        try {
            List<Tweet> tweetList = tweetRepository.findAll();
            if (tweetList != null) {
                Collections.sort(tweetList, new Comparator<Tweet>() {
                    public int compare(Tweet t1, Tweet t2) {
                        return t2.getCreated().compareTo(t1.getCreated());
                    }
                });
                log.info("Ending getAll tweet method");
                return new ResponseEntity<>(tweetList, HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new PersistenceException(ApplicationConstants.FAILED, ApplicationConstants.TWEET_ERROR_ID);
        }
        log.info("Ending getAll tweet method");
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Tweet>> getAllTweetsByUser(final String userName) throws PersistenceException {
        log.info("Starting getAll tweet by user method");
        try {
            if (validUser(userName)) {
                final List<Tweet> tweetListByUser = tweetRepository.findByUserName(userName);
                if (tweetListByUser != null) {
                    log.info("Ending getAll tweet by user method");
                    Collections.sort(tweetListByUser, new Comparator<Tweet>() {
                        public int compare(Tweet t1, Tweet t2) {
                            return t2.getCreated().compareTo(t1.getCreated());
                        } 
                    });
                    return new ResponseEntity<>(tweetListByUser, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new PersistenceException(ApplicationConstants.FAILED, ApplicationConstants.TWEET_ERROR_ID);
        }
        log.info("Ending getAll tweet by user method");
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> likeTweet(String userName, int tweetId) throws PersistenceException {
        log.info("Starting like tweet method");
        try {
            if (validUser(userName) && validTweet(tweetId)) {
                Optional<Tweet> findById = tweetRepository.findById(tweetId);
                if (findById.isPresent()) {
                    Tweet tweet = findById.get();
                    final Map<String, Integer> likesMap = tweet.getLikes();
                    if (likesMap != null) {
                        likesMap.put(userName, 1);
                        tweet.setLikes(likesMap);
                    } else {
                        final Map<String, Integer> newLikeMap = new HashMap<>();
                        newLikeMap.put(userName, 1);
                        tweet.setLikes(newLikeMap);
                    }
                    Query query = new Query();
                    query.addCriteria(Criteria.where(ApplicationConstants.TWEET_ID).is(tweetId));
                    Update update = new Update();
                    update.set(ApplicationConstants.LIKES, tweet.getLikes());
                    tweet = mongoOperations.findAndModify(query, update, Tweet.class);
                    if (tweet != null) {
                        log.info("Ending like tweet method");
                        return new ResponseEntity<>(ApplicationConstants.LIKED_TWEET, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            throw new PersistenceException(ApplicationConstants.FAILED, ApplicationConstants.TWEET_ERROR_ID);
        }
        log.info("Ending like tweet method");
        return new ResponseEntity<>(ApplicationConstants.FAILED, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GenericResponse> postTweet(String userName, TweetRequest request) throws PersistenceException {
        log.info("Starting post tweet method");
        GenericResponse response = new GenericResponse();
        try {
            if (validUser(userName) && request != null) {
                List<Tweet> tweetList = tweetRepository.findAll();
                if (tweetList != null && tweetList.size() >= 0) {
                    Collections.sort(tweetList, new Comparator<Tweet>() {
                        public int compare(Tweet t1, Tweet t2) {
                            return t2.getCreated().compareTo(t1.getCreated());
                        }
                    });
                    long idCount = 1;
                    if (tweetList.size() > 0) {
                        idCount = tweetList.get(0).getTweetId() + 1;
                    }
                    final Tweet tweet = new Tweet(((int) idCount), request.getUserName(),
                            request.getTweet(),
                            new Date(System.currentTimeMillis()), new HashMap<String, Integer>(), null);
                    tweetRepository.save(tweet);
                    // producer.sendMessage("Tweet Added with the following tweetId" +
                    // tweet.getTweetId());
                    log.info("Ending post tweet method");
                    response.setGenericResponse(ApplicationConstants.SUCCESS);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new PersistenceException(ApplicationConstants.FAILED, ApplicationConstants.TWEET_ERROR_ID);
        }
        log.info("Ending post tweet method");
        response.setGenericResponse(ApplicationConstants.FAILED);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> reply(String userName, int tweetId, String reply) throws PersistenceException {
        log.info("Starting reply method");
        try {
            if (validUser(userName) && validTweet(tweetId)) {
                Optional<Tweet> tweetById = tweetRepository.findById(tweetId);
                if (tweetById.isPresent()) {
                    Tweet tweet = tweetById.get();
                    Map<String, List<String>> replyMap = tweet.getReplies();
                    if (replyMap != null) {
                        if (replyMap.containsKey(userName)) {
                            replyMap.get(userName).add(reply);
                            tweet.setReplies(replyMap);
                        } else {
                            replyMap.put(userName, Arrays.asList(reply));
                            tweet.setReplies(replyMap);
                        }
                    } else {
                        final Map<String, List<String>> newReplyMap = new HashMap<>();
                        newReplyMap.put(userName, Arrays.asList(reply));
                        tweet.setReplies(newReplyMap);
                    }
                    Query query = new Query();
                    query.addCriteria(Criteria.where(ApplicationConstants.TWEET_ID).is(tweetId));
                    Update update = new Update();
                    update.set(ApplicationConstants.REPLIES, tweet.getReplies());

                    tweet = mongoOperations.findAndModify(query, update, Tweet.class);
                    if (tweet != null) {
                        log.info("Ending reply method");
                        return new ResponseEntity<>(ApplicationConstants.SUCCESS, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            throw new PersistenceException(ApplicationConstants.FAILED, ApplicationConstants.TWEET_ERROR_ID);
        }
        log.info("Ending reply method");
        return new ResponseEntity<>(ApplicationConstants.FAILED, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> updateTweet(String userName, int tweetId, TweetRequest request)
            throws PersistenceException {
        log.info("Starting update tweet method");
        try {
            if (validUser(userName) && validTweet(tweetId)) {
                Optional<Tweet> tweetById = tweetRepository.findById(tweetId);
                if (tweetById.isPresent()) {
                    Tweet tweet = tweetById.get();
                    Query query = new Query();
                    query.addCriteria(Criteria.where(ApplicationConstants.TWEET_ID).is(tweetId));
                    Update update = new Update();
                    update.set(ApplicationConstants.TWEET, request.getTweet());

                    tweet = mongoOperations.findAndModify(query, update, Tweet.class);
                    if (tweet != null) {
                        log.info("Ending reply method");
                        return new ResponseEntity<>(ApplicationConstants.SUCCESS, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            throw new PersistenceException(ApplicationConstants.FAILED, ApplicationConstants.TWEET_ERROR_ID);
        }
        log.info("Ending update tweet method");
        return new ResponseEntity<>(ApplicationConstants.FAILED, HttpStatus.OK);
    }

    private boolean validUser(final String username) {
        if (username != null) {
            Optional<User> user = userRepository.findByEmailId(username);
            if (user.isPresent()) {
                log.info("User is valid " + username);
                return true;
            }
        }
        log.info("User is invalid " + username);
        return false;
    }

    private boolean validTweet(final int tweetId) {
        if (tweetId > 0) {
            Optional<Tweet> tweet = tweetRepository.findById(tweetId);
            if (tweet.isPresent()) {
                log.info("Tweet is valid " + tweetId);
                return true;
            }
        }
        log.info("Tweet is invalid " + tweetId);
        return false;
    }

}
