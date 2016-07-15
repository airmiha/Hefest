package com.hefest.app.dao;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hefest.app.model.User;
import com.hefest.app.security.PersistentTokenDAO;
import com.hefest.app.security.SecurityUtils;
import com.hefest.app.service.SecurityService;

@Component
public class UserDAO {
	
    private final Logger log = LoggerFactory.getLogger(UserDAO.class);
    
    // Token is valid for two days
    private static final int RESET_PASSWORD_TOKEN_VALIDITY_DAYS = 2;
	
	private NamedParameterJdbcTemplate jdbcTemplateObject;
	
    @Autowired
    private PasswordEncoder passwordEncoder;
		
	@Autowired
    private PersistentTokenDAO persistentTokenDAO;
	
	@Autowired
	SecurityService securityService;
	
	private SimpleJdbcInsert insertUser;
	private SimpleJdbcInsert insertAddress;
	
	@Autowired
	public UserDAO(NamedParameterJdbcTemplate jdbcTemplateObject, DataSource dataSource) {
		this.jdbcTemplateObject = jdbcTemplateObject;
		this.insertUser = new SimpleJdbcInsert(dataSource).withTableName("professionals.users").usingColumns("name","email","password","addressid", "activationkey", "active", "role").usingGeneratedKeyColumns("userid");
		this.insertAddress = new SimpleJdbcInsert(dataSource).withTableName("professionals.addresses").usingColumns("addressline").usingGeneratedKeyColumns("addressid");
	
		//Add a default user as admin
		//TODO: Configure this through Liquibase
		User user = new User();
		user.setName("Admin");
		user.setEmail("admin");
		user.setPassword("admin");
		
	}

	public User getUserByEmail(String email) {
		 String SQL = "SELECT professionals.users.*  FROM professionals.users WHERE users.email =:email";
		 Map<String, Object> paramMap = new HashMap<String, Object>();
	     paramMap.put("email", email);	
	     try {
	    	return (User) jdbcTemplateObject.queryForObject(SQL, paramMap, new BeanPropertyRowMapper<User>(User.class));
	     } catch (EmptyResultDataAccessException e) {
	    	return null; 
	     }
	}
	
	@Transactional
	public User insertUser(User newUser) {
		Map<String, Object> addressParameters = new HashMap<String, Object>();
		addressParameters.put("addressline", "");
		Number addressId = insertAddress.executeAndReturnKey(addressParameters);
		newUser.setAddressId(addressId.intValue());
		String encryptedPassword = passwordEncoder.encode(newUser.getPassword());
		newUser.setPassword(encryptedPassword);
        newUser.setActive(false);
        SecureRandom random = new SecureRandom();
        String randomActivationKey = new BigInteger(130, random).toString(32);
        newUser.setActivationKey(randomActivationKey);
		Number userId = insertUser.executeAndReturnKey(new BeanPropertySqlParameterSource(newUser)); 		
		newUser.setUserId(userId.intValue()); 
		log.info("Inserted user with e-mail {}", newUser.getEmail());
		return newUser;
	}

	@Transactional
	public User activateRegistration(String key) {
		log.debug("Activating user for activation key {}", key);
		String SQL = "SELECT professionals.users.*  FROM professionals.users WHERE users.activationkey=:activationKey";
		Map<String, Object> paramMap = new HashMap<String, Object>();
	    paramMap.put("activationKey", key);			
    	User user = jdbcTemplateObject.queryForObject(SQL, paramMap, new BeanPropertyRowMapper<User>(User.class));
    	if (user != null) {
    		String updateSQL = "UPDATE professionals.users SET active=true, activationkey=null WHERE users.activationkey=:activationKey"; 
    		jdbcTemplateObject.update(updateSQL, paramMap);
	        log.info("Activated user with e-mail: {}", user.getEmail());
	    }
    	return user;
	}
	
	@Transactional
	public User generateResetPasswordTokenForUser(User user) {
		SecureRandom random = new SecureRandom();
        String randomResetKey = new BigInteger(130, random).toString(32);
		user.setResetPasswordToken(randomResetKey);
		user.setResetPasswordTokenLocalDate(LocalDate.now());
		log.info("Resetting password token for user with email {} ...", user);	
    	String updateSQL = "UPDATE professionals.users SET resetPasswordToken=:resetPasswordToken, resetPasswordTokenDate=:resetPasswordTokenDate WHERE users.email=:email"; 
		jdbcTemplateObject.update(updateSQL, new BeanPropertySqlParameterSource(user));
        log.info("Resetted password token for user with email {} ...", user.getEmail());		    
    	return user;
	}
	
   public boolean isPasswordValid(String password) {
	   User user = getUserByEmail(securityService.getCurrentLogin());
	   if (user != null) {
		  return passwordEncoder.matches(password, user.getPassword());  
	   }
	   return false;
    }
	
   public void changePassword(String password) {
	   User user = getUserByEmail(SecurityUtils.getCurrentLogin());
	   if (user != null) {
		   	String encryptedPassword = passwordEncoder.encode(password);
		   	String updateSQL = "UPDATE professionals.users SET password=:password WHERE users.email=:email";
		   	Map<String, Object> paramMap = new HashMap<String, Object>();
		   	paramMap.put("email", user.getEmail());
		    paramMap.put("password", encryptedPassword);
   			jdbcTemplateObject.update(updateSQL, paramMap);
   			log.info("Changed password for user with e-mail: {}", user.getEmail());
	   }
    }
   
    @Transactional
	public User resetPassword(String token, String password) {
		String SQL = "SELECT professionals.users.*  FROM professionals.users WHERE users.resetPasswordToken=:token";		 
		Map<String, Object> paramMap = new HashMap<String, Object>();
	    paramMap.put("token", token);		   
		User user = jdbcTemplateObject.queryForObject(SQL, paramMap, new BeanPropertyRowMapper<User>(User.class));
		String updateSQL = "UPDATE professionals.users SET resetPasswordToken=null, resetPasswordTokenDate=null WHERE users.resetPasswordToken=:token"; 
		jdbcTemplateObject.update(updateSQL, paramMap);
    	if (user.getResetPasswordTokenLocalDate().plusDays(RESET_PASSWORD_TOKEN_VALIDITY_DAYS).isBefore(LocalDate.now())) {
    		log.warn("Reset password token for user with email: {} has expired.", user.getEmail());
   			return null;
    	} 
    	String encryptedPassword = passwordEncoder.encode(password);
		paramMap.put("password", encryptedPassword);	
		paramMap.put("email", user.getEmail());
	   	updateSQL = "UPDATE professionals.users SET password=:password WHERE users.email=:email";
	   	jdbcTemplateObject.update(updateSQL, paramMap);
	   	return user; 	
	}
}
