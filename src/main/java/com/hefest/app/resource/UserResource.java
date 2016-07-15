package com.hefest.app.resource;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hefest.app.dao.UserDAO;
import com.hefest.app.model.User;
import com.hefest.app.service.MailService;

@RestController
@RequestMapping("/resources")
public class UserResource {
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private UserDAO userDAO;

	@RequestMapping(value= "/users", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<?> insertUser(@Valid @RequestBody User user, HttpServletRequest request,
            HttpServletResponse response) {
		User existingUser = userDAO.getUserByEmail(user.getEmail());
		if (existingUser != null) {
			 return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		} else {
			User newUser = userDAO.insertUser(user);
			final Locale locale = new Locale("hr");
			String content = mailService.createHtmlContentFromTemplate(MailService.EMAIL_ACTIVATION_PREFIX, newUser, locale, request, response);
			mailService.sendActivationEmail(user.getEmail(), content, locale);			
			return new ResponseEntity<>(HttpStatus.CREATED);
		} 
	}
	
}
