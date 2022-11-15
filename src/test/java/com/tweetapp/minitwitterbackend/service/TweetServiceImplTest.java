package com.tweetapp.minitwitterbackend.service;

import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//import com.tweetapp.minitwitterbackend.config.KafkaProducerConfig;
import com.tweetapp.minitwitterbackend.dto.Tweet;
import com.tweetapp.minitwitterbackend.dto.User;
import com.tweetapp.minitwitterbackend.exception.PersistenceException;
import com.tweetapp.minitwitterbackend.repository.TweetRepo;
import com.tweetapp.minitwitterbackend.repository.UserRepo;
import com.tweetapp.minitwitterbackend.request.TweetRequest;
import com.tweetapp.minitwitterbackend.utility.ApplicationConstants;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TweetServiceImplTest {

    @InjectMocks
    TweetServiceImpl tweetService;

    @Mock
    UserRepo userRepository;

    @Mock
    TweetRepo tweetRepository;

    @Mock
    MongoOperations mongoperation;

    // @Mock
    // KafkaProducerConfig producer;

    @Test
    void postTweet() throws PersistenceException {
        TweetRequest tweetReq = new TweetRequest();
        tweetReq.setUserName("ayush@gmail.com");
        tweetReq.setTweetId(1);
        tweetReq.setTweet("test-tweet");
        List<Tweet> tweetList = Arrays.asList(new Tweet(1, "Hello", "ayush@gmail.com", new Date(), null, null),
                new Tweet(2, "ayush@gmail.com", "test-tweet", new Date(), null, null));
        Mockito.when(tweetRepository.findAll()).thenReturn(tweetList);
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
        Assertions.assertEquals(ApplicationConstants.SUCCESS,
                tweetService.postTweet("ayush@gmail.com", tweetReq).getBody().getGenericResponse());
    }

    @Test
    void postTweetUserNotPresent() throws PersistenceException {
        TweetRequest tweetReq = new TweetRequest();
        tweetReq.setUserName("ayush@gmail.com");
        tweetReq.setTweetId(1);
        tweetReq.setTweet("test-tweet");
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.empty());
        Assertions.assertEquals(ApplicationConstants.FAILED,
                tweetService.postTweet("ayush@gmail.com", tweetReq).getBody().getGenericResponse());
    }

    @Test
    void getAllTweets() throws PersistenceException {
        List<Tweet> tweetList = Arrays.asList(new Tweet(1, "Hello", "ayush@gmail.com", new Date(), null, null),
                new Tweet(2, "ayush@gmail.com", "test-tweet", new Date(), null, null));
        Mockito.when(tweetRepository.findAll()).thenReturn(tweetList);
        Assertions.assertEquals(2, tweetService.getAllTweets().getBody().size());
    }

    @Test
    void getAllTweetsFailed() throws PersistenceException {
        Mockito.when(tweetRepository.findAll()).thenReturn(null);
        Assertions.assertEquals(0, tweetService.getAllTweets().getBody().size());
    }

    @Test
    void getAllTweetByUser() throws PersistenceException {
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(tweetRepository.findByUserName("ayush@gmail.com"))
                .thenReturn(Arrays.asList(new Tweet(1, "ayush@gmail.com", "test-tweet1", new Date(), null, null),
                        new Tweet(1, "ayush@gmail.com", "test-tweet2", new Date(), null, null)));
        Assertions.assertEquals(2, tweetService.getAllTweetsByUser("ayush@gmail.com").getBody().size());
    }

    @Test
    void getAllUserTweetFailed() throws PersistenceException {
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(tweetRepository.findByUserName("ayush@gmail.com")).thenReturn(null);
        Assertions.assertEquals(0, tweetService.getAllTweetsByUser("ayush@gmail.com").getBody().size());
    }

    @Test
    void updateTweet() throws PersistenceException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(new Tweet());
        TweetRequest tweetReq = new TweetRequest();
        tweetReq.setUserName("ayush@gmail.com");
        tweetReq.setTweetId(1);
        tweetReq.setTweet("test-tweet");
        Assertions.assertEquals(ApplicationConstants.SUCCESS, tweetService.updateTweet("ayush@gmail.com", 1,
                tweetReq).getBody());
    }

    @Test
    void updateTweetFailed() throws PersistenceException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(null);
        TweetRequest tweetReq = new TweetRequest();
        tweetReq.setUserName("ayush@gmail.com");
        tweetReq.setTweetId(1);
        tweetReq.setTweet("test-tweet");
        Assertions.assertEquals(ApplicationConstants.FAILED, tweetService.updateTweet("ayush@gmail.com", 1,
                tweetReq).getBody());
    }

    @Test
    void deleteTweet() throws PersistenceException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
        Assertions.assertEquals(ApplicationConstants.TWEET_DELETED,
                tweetService.deleteTweet("ayush@gmail.com", 1).getBody().getGenericResponse());
    }

    @Test
    void deleteTweetFailed() throws PersistenceException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
        Assertions.assertEquals(ApplicationConstants.FAILED, tweetService.deleteTweet("ayush@gmail.com", 1).getBody().getGenericResponse());
    }

    @Test
    void replyTweet() throws PersistenceException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(new Tweet());
        Assertions.assertEquals(ApplicationConstants.SUCCESS,
                tweetService.reply("ayush@gmail.com", 1, "test-reply").getBody());
    }

    @Test
    void replyTweetFailed() throws PersistenceException {
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.empty());
        Assertions.assertEquals(ApplicationConstants.FAILED,
                tweetService.reply("ayush@gmail.com", 1, "test-reply").getBody());
    }

    @Test
    void likeTweet() throws PersistenceException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(new Tweet());
        Assertions.assertEquals(ApplicationConstants.LIKED_TWEET, tweetService.likeTweet("ayush@gmail.com", 1).getBody());
    }

    @Test
    void likeTweetFailed() throws PersistenceException {
        Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
        Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(null);
        Assertions.assertEquals(ApplicationConstants.FAILED, tweetService.likeTweet("ayush@gmail.com", 1).getBody());
    }

}