package com.tweetapp.minitwitterbackend.controller;

import java.util.List;

import javax.validation.Valid;

import com.tweetapp.minitwitterbackend.dto.GenericResponse;
import com.tweetapp.minitwitterbackend.dto.User;
import com.tweetapp.minitwitterbackend.exception.PersistenceException;
import com.tweetapp.minitwitterbackend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Generated;

@RequestMapping(value = "/api/v1.0/tweets")
@RestController
@Generated
//@CrossOrigin(origins = "https://mini-twitter-frontend.azurewebsites.net")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping(value = "/login")
	public ResponseEntity<String> login(@RequestParam("emailId") String emailId,
			@RequestParam("password") String password) throws PersistenceException {
		return userService.login(emailId, password);
	}

	@PostMapping(value = "/register")
	public ResponseEntity<GenericResponse> registerUser(@RequestBody @Valid User user) throws PersistenceException {
		return userService.register(user);
	}

	@GetMapping(value = "/users/search")
	public ResponseEntity<User> getUser(@RequestParam("userName") String userName) throws PersistenceException {
		return userService.getUser(userName);
	}

	@GetMapping(value = "/users/all")
	public ResponseEntity<List<User>> getAllUser() throws PersistenceException {
		return userService.getAllUser();
	}

	@GetMapping(value = "/forgot")
	public ResponseEntity<String> forgotPassword(@RequestParam("userName") String userName,
			@RequestParam("newPassword") String password) throws PersistenceException {

		return userService.forgotPassword(userName, password);
	}

}
