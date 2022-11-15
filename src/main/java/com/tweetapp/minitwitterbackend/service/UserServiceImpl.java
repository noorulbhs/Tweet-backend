package com.tweetapp.minitwitterbackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tweetapp.minitwitterbackend.dto.GenericResponse;
//import com.tweetapp.configuration.KafkaProducerConfig;
import com.tweetapp.minitwitterbackend.dto.User;
import com.tweetapp.minitwitterbackend.exception.PersistenceException;
import com.tweetapp.minitwitterbackend.repository.UserRepo;
import com.tweetapp.minitwitterbackend.utility.ApplicationConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepo userRepository;

	@Autowired
	MongoOperations mongoperation;

	// @Autowired
	// private KafkaProducerConfig producer;

	public ResponseEntity<String> login(final String userName, final String password) throws PersistenceException {
		log.info("Starting login method for user " + userName);
		String userValid = "FALSE";
		System.out.println(userName+"           "+password);
		try {
			if (!userName.isEmpty() && !password.isEmpty()) {
				Optional<User> isValid = userRepository.findByEmailIdAndPassword(userName, password);

				//System.out.println(userRepository.findAll().toString());
				userValid = isValid.isPresent() ? ApplicationConstants.LOGIN_SUCCESS : ApplicationConstants.LOGIN_FAILED;
			}
		} catch (Exception e) {
			throw new PersistenceException(ApplicationConstants.NO_USERS_FOUND, ApplicationConstants.USER_ERROR_ID);
		}
		log.info("Ending login method");
		return new ResponseEntity<>(userValid, HttpStatus.OK);
	}

	public ResponseEntity<GenericResponse> register(final User user) throws PersistenceException {
		log.info("Starting Register for new user : " + user.getEmailId());
		String responseMessage = ApplicationConstants.FAILED;
		GenericResponse response = new GenericResponse();
		try {
			if (user != null) {
				Optional<User> isValid = userRepository.findByEmailId(user.getEmailId());
				responseMessage = isValid.isPresent() ? ApplicationConstants.USER_NAME_ALREADY_EXIST
						: ApplicationConstants.USER_NAME_REGISTERED_SUCCESSFULLY;
				if (isValid.isPresent()) {
					responseMessage = ApplicationConstants.USER_NAME_ALREADY_EXIST;
				} else {
					userRepository.save(user);
					responseMessage = ApplicationConstants.USER_NAME_REGISTERED_SUCCESSFULLY;
				}
				response.setGenericResponse(responseMessage);
			}
		} catch (Exception e) {
			throw new PersistenceException(ApplicationConstants.USER_NAME_ALREADY_EXIST, ApplicationConstants.USER_ERROR_ID);
		}
		log.info("Ending register Method");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<User> getUser(final String userName) throws PersistenceException {
		log.info("Starting getUser method for user : " + userName);
		User userResponse = new User();
		try {
			if (!userName.isEmpty()) {
				Optional<User> userPresent = userRepository.findByEmailId(userName);
				if (userPresent.isPresent()) {
					userResponse = userPresent.get();
				}
			}
		} catch (Exception e) {
			throw new PersistenceException(ApplicationConstants.NO_USERS_FOUND, ApplicationConstants.USER_ERROR_ID);
		}
		log.info("Ending getUser method");
		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	public ResponseEntity<List<User>> getAllUser() throws PersistenceException {
		log.info("Starting getAllUser method");
		try {
			List<User> findAll = userRepository.findAll();
			if (!findAll.isEmpty()) {
				return new ResponseEntity<>(findAll, HttpStatus.OK);
			}
		} catch (Exception e) {
			throw new PersistenceException(ApplicationConstants.NO_USERS_FOUND, ApplicationConstants.USER_ERROR_ID);
		}
		log.info("Ending getAllUser Method");
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	}

	public ResponseEntity<String> forgotPassword(final String userName, final String password)
			throws PersistenceException {
		log.info("Starting Forgot Password method for user : " + userName);
		String responseMessage = ApplicationConstants.FAILED;
		try {
			if (!userName.isEmpty() && !password.isEmpty()) {
				Optional<User> findByEmailIdName = userRepository.findByEmailId(userName);
				if (!findByEmailIdName.isPresent()) {
					return new ResponseEntity<>(ApplicationConstants.NO_USERS_FOUND, HttpStatus.OK);
				}
				// producer.sendMessage("Forgot Password for :: " + userName.concat(" " +
				// password));
				Query query = new Query();
				query.addCriteria(Criteria.where(ApplicationConstants.EMAIL_ID).is(userName));

				Update update = new Update();
				update.set(ApplicationConstants.PASSWORD, password);

				User user = mongoperation.findAndModify(query, update, User.class);
				if (user != null) {
					responseMessage = ApplicationConstants.PASSWORD_UPDATED;
				}
			}
		} catch (Exception e) {
			throw new PersistenceException(ApplicationConstants.ERROR_WHILE_UPDATING_PASSWORD,
					ApplicationConstants.USER_ERROR_ID);
		}
		log.info("Ending forgot password method");
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}

}
