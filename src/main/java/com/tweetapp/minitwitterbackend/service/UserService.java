package com.tweetapp.minitwitterbackend.service;

import java.util.List;

import com.tweetapp.minitwitterbackend.dto.GenericResponse;
import com.tweetapp.minitwitterbackend.dto.User;
import com.tweetapp.minitwitterbackend.exception.PersistenceException;

import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<String> login(String userName, String password) throws PersistenceException;

    ResponseEntity<GenericResponse> register(User user) throws PersistenceException;

    ResponseEntity<User> getUser(String userName) throws PersistenceException;

    ResponseEntity<List<User>> getAllUser() throws PersistenceException;

    ResponseEntity<String> forgotPassword(String userName, String password) throws PersistenceException;
}
