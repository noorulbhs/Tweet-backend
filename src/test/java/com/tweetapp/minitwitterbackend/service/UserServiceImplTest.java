package com.tweetapp.minitwitterbackend.service;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.tweetapp.minitwitterbackend.dto.GenericResponse;
import com.tweetapp.minitwitterbackend.dto.User;
import com.tweetapp.minitwitterbackend.exception.PersistenceException;
import com.tweetapp.minitwitterbackend.repository.UserRepo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	@Mock
	UserRepo userRepository;

	@InjectMocks
	UserServiceImpl userService;

	@Mock
	MongoOperations mongoperation;

	@Test
	void loginTest() throws PersistenceException {
		Mockito.when(userRepository.findByEmailIdAndPassword("ayush@gmail.com", "1"))
				.thenReturn(Optional.of(new User()));
		ResponseEntity<String> login = userService.login("ayush@gmail.com", "1");
		Assertions.assertEquals("Login Success",login.getBody());
	}

	@Test
	void loginTestFailed() throws PersistenceException {
		Mockito.when(userRepository.findByEmailIdAndPassword("ayush@gmail.com", "1"))
				.thenReturn(Optional.empty());
		Assertions.assertEquals("Login Failed", userService.login("ayush@gmail.com", "1").getBody());
	}

	@Test
	void registerTest() throws PersistenceException {
		User user = new User();
		user.setUserId(1);
		user.setPassword("abc123");
		user.setLastName("singh");
		user.setFirstName("ayush");
		user.setEmailId("ayush@gmail.com");
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.empty());
		ResponseEntity<GenericResponse> response = userService.register(user);
		Assertions.assertEquals("UserName Registered Successfully", response.getBody().getGenericResponse());
	}

	@Test
	void registerTestFailed() throws PersistenceException {
        User user = new User();
		user.setUserId(1);
		user.setPassword("abc123");
		user.setLastName("singh");
		user.setFirstName("ayush");
		user.setEmailId("ayush@gmail.com");		
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(user));
		Assertions.assertEquals("UserName Already Exist", userService.register(user).getBody().getGenericResponse());
	}

	@Test
	void forgotPasswordTestUserNotFound() throws PersistenceException {
        Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.empty());
		Assertions.assertEquals("No Users Found", userService.forgotPassword("ayush@gmail.com", "123").getBody());
	}

	@Test
	void forgotPasswordTest() throws PersistenceException {
		Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
		Mockito.when(mongoperation.findAndModify(any(), any(), any())).thenReturn(new User());
		ResponseEntity<String> response = userService.forgotPassword("ayush@gmail.com","123");
		Assertions.assertEquals("Password Updated",response.getBody());
	}

	@Test
	void getUserFailureTest() throws PersistenceException {
		Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.empty());
		Assertions.assertNotNull(userService.getUser("ayush@gmail.com").getBody());
	}

	@Test
	void getUserTest() throws PersistenceException {
		Mockito.when(userRepository.findByEmailId("ayush@gmail.com")).thenReturn(Optional.of(new User()));
		Assertions.assertNotNull(userService.getUser("ayush@gmail.com"));
	}

	@Test
	void getAllUsersFailureTest() throws PersistenceException {
		Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>());
		Assertions.assertEquals(0,userService.getAllUser().getBody().size());
	}

	@Test
	void getAllusersTest() throws PersistenceException {
        User user = new User();
		user.setUserId(1);
		user.setPassword("abc123");
		user.setLastName("singh");
		user.setFirstName("ayush");
		user.setEmailId("ayush@gmail.com");
        User user2 = new User();
		user.setUserId(1);
		user.setPassword("abc123");
		user.setLastName("singh");
		user.setFirstName("ayush");
		user.setEmailId("ayush55@gmail.com");
		List<User> userList = Arrays.asList(user,user2);
		Mockito.when(userRepository.findAll()).thenReturn(userList);
		ResponseEntity<List<User>> response = userService.getAllUser();
		Assertions.assertEquals(2,response.getBody().size());
	}

}
