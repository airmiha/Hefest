package com.hefest.app.resource;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hefest.app.dao.ProfessionalDAO;
import com.hefest.app.dao.ServicesDAO;
import com.hefest.app.dao.UserDAO;
import com.hefest.app.model.MultiResponse;
import com.hefest.app.model.Professional;
import com.hefest.app.model.Response;
import com.hefest.app.model.Testimonial;
import com.hefest.app.model.User;
import com.hefest.app.security.SecurityUtils;
import com.hefest.app.service.MailService;

@RestController
@RequestMapping("/resources")
public class ProfessionalsResource {
	
	private final Logger log = LoggerFactory.getLogger(ProfessionalsResource.class);
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private ServicesDAO servicesDAO;
	
	@Autowired
	private ProfessionalDAO professionalDAO;
	
	@RequestMapping("/professionals")
	@Timed
	public MultiResponse<String> professionals(@RequestParam(value="tags", required = false) List<String> tags, @RequestParam Map<String, String> params) {
		return professionalDAO.getProfessionals(tags, params);
	}
	
	@RequestMapping("/professionals/{professionalid}")
	@Timed
	public Professional professional(@PathVariable Integer professionalid) {
		return professionalDAO.getProfessional(professionalid);
	}
	
	@RequestMapping("/professionals/current")
	@Timed
	public ResponseEntity<Professional> currentProfessional() {
		try {
			int professionalId = professionalDAO.getProfessionalId(SecurityUtils.getCurrentLogin());
			Professional professional = professionalDAO.getProfessional(professionalId);
			log.debug("Successfuly fetched data for logged in professional: '{}' with Id '{}'", professional.getName(), professional.getProfessionalId());
			return new ResponseEntity<Professional>(professional, HttpStatus.OK);
		} catch (EmptyResultDataAccessException e) {
			log.debug("Failed to fetch data for logged in professional: '{}'", SecurityUtils.getCurrentLogin());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value= "/professionals/current", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<?> updateProfessional(@RequestBody Professional professional) {
		try {
			int professionalId = professionalDAO.getProfessionalId(SecurityUtils.getCurrentLogin());
			professional.setProfessionalId(professionalId);
			try {
				professionalDAO.updateProfessional(professional);
			} catch (DataAccessException e) {
				log.debug("Failed to update data for logged in professional: '{}' with Id '{}'. Exceptin is: {} ", SecurityUtils.getCurrentLogin(), professional.getProfessionalId(), e.getLocalizedMessage());
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}			
			log.debug("Successfuly updated data for logged in professional: '{}' with Id '{}'.", SecurityUtils.getCurrentLogin(), professional.getProfessionalId());
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (EmptyResultDataAccessException e) {
			log.debug("Failed to resolve ID of logged in professional: '{}'. {}", SecurityUtils.getCurrentLogin());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} 
	}
	
	
	@RequestMapping(value= "/professionals", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<?> insertProfessional(@Valid @RequestBody User user, HttpServletRequest request,
            HttpServletResponse response) {
		User existingUser = userDAO.getUserByEmail(user.getEmail());
		if (existingUser != null) {
			 return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		} else {
			User newUser = professionalDAO.insertProfessional(user);
			final Locale locale = new Locale("hr");
			String content = mailService.createHtmlContentFromTemplate(MailService.EMAIL_ACTIVATION_PREFIX, newUser, locale, request, response);
			mailService.sendActivationEmail(user.getEmail(), content, locale);			
			return new ResponseEntity<>(HttpStatus.CREATED);
		} 
	}
	
	@RequestMapping(value= "/professionsAsTree")
	@Timed
	public Map<Integer, Map<String, Object>> getProfessionsTree() {
		return servicesDAO.getProfessionsAndCategories();
	}
	
	@RequestMapping(value= "/professions")
	@Timed
	public List<Map<String, Object>> getProfessions() {
		return servicesDAO.getProfessions();
	}
	
	@RequestMapping(value= "/professions/{professionid}/servicecategories")
	@Timed
	public List<Map<String, Object>> getServiceCategories(@PathVariable Integer professionid) {
		return servicesDAO.getServiceCategories(professionid);
	}
	
	@RequestMapping(value= "/servicecategories/{servicecategoryid}")
	@Timed
	public Map<String, Object> getServiceCategory(@PathVariable Integer servicecategoryid) {
		return servicesDAO.getServiceCategory(servicecategoryid);
	}
	
	@RequestMapping(value= "/items")
	@Timed
	public List<Map<String, Object>> getItems(@RequestParam(value="tags", required = false) List<String> tags) {
		return servicesDAO.getItems(tags);
	}
	
	@RequestMapping(value= "/professionals/{professionalid}/endorsements")
	@Timed
	public Response<Map<String, Object>> getEndorsements(@PathVariable Integer professionalid, @RequestParam Map<String, String> parameters) {
		return professionalDAO.getEndorsements(professionalid, parameters);
	}
	
	@RequestMapping(value= "/professionals/{professionalid}/testimonials")
	@Timed
	public Response<Testimonial> getTestimonials(@PathVariable Integer professionalid, Map<String, String> parameters) {
		return professionalDAO.getTestimonials(professionalid, parameters);
	}
}
