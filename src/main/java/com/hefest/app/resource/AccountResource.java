package com.hefest.app.resource;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hefest.app.dao.ProfessionalDAO;
import com.hefest.app.dao.UserDAO;
import com.hefest.app.model.User;
import com.hefest.app.service.MailService;
import com.hefest.app.service.SecurityService;

@RestController
@RequestMapping("/resources/account")
public class AccountResource {
	
	private final Logger log = LoggerFactory.getLogger(AccountResource.class);
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private ProfessionalDAO professionalDAO;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	SecurityService securityService;
	
	/**
     * GET  /account -> get the current user.
     */
    @RequestMapping(value = "/current",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User> getAccount() {
    	log.debug("Get account");
        User user = userDAO.getUserByEmail(securityService.getCurrentLogin());
        if (user == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User returnUser = new User();
        returnUser.setName(user.getName());
        returnUser.setEmail(user.getEmail());
        returnUser.setRole(user.getRole());
        return new ResponseEntity<>(returnUser, HttpStatus.OK);
    }
	
	/**
     * GET  /rest/authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        return request.getRemoteUser();
    }
	
    @RequestMapping(value = "/activate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> activateAccount(@RequestParam(value="key") String key) {
        userDAO.activateRegistration(key);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/forgotpassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> forgotPassword(@RequestBody String email, HttpServletRequest request,
            HttpServletResponse response) {
    	log.info("Password reset requested for email '{}'!", email);
        if (email.isEmpty()) {
        	log.warn("Password reset requested with empty e-mail.");   	
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
        	log.warn("Reset password e-mail could not be sent to user with e-mail '{}'. User not found.", email); 
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    	userDAO.generateResetPasswordTokenForUser(user);
    	final Locale locale = new Locale("hr");
 		String content = mailService.createHtmlContentFromTemplate(MailService.EMAIL_RESETPASSWORD_PREFIX, user, locale, request, response);	     		
 		try {
 			mailService.sendResetPasswordEmail(user.getEmail(), content, locale);
 			log.info("Sent reset password e-mail to User '{}'!", user.getEmail());
 			return new ResponseEntity<>(HttpStatus.OK);
 		} catch (Exception e) {
 	       log.warn("Reset password e-mail could not be sent to user '{}', exception is: {}", user.getEmail(), e.getMessage());   	
 	       return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
 		}   		 
    }
	
    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> passwordAndToken) {
    	String password = passwordAndToken.get("password");
    	String token = passwordAndToken.get("token");
       	log.info("Resetting password for token '{}'!", token);
    	if (StringUtils.isEmpty(password) || StringUtils.isEmpty(token)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }    	
        User user = userDAO.resetPassword(token, password);
        if (user == null) {
        	return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/changepassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> oldAndNewPassword) {
    	String oldPassword = oldAndNewPassword.get("oldPassword");
    	String newPassword = oldAndNewPassword.get("newPassword");
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (userDAO.isPasswordValid(oldPassword)) {
        	userDAO.changePassword(newPassword);
        } else {
        	log.warn("Old password does not match for User: {}. Rejecting password change request.", securityService.getCurrentLogin());
        	return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }    
    	return new ResponseEntity<>(HttpStatus.OK);
    }
}
