package com.hefest.app.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hefest.app.dao.ServiceRequestDAO;
import com.hefest.app.dao.UserDAO;
import com.hefest.app.model.ServiceRequest;
import com.hefest.app.model.User;
import com.hefest.app.security.SecurityUtils;
import com.hefest.app.service.MailService;

@RestController
@RequestMapping("/resources")
public class ServiceRequestResource {
	
	private final Logger log = LoggerFactory.getLogger(ServiceRequestDAO.class);
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private ServiceRequestDAO serviceRequestDAO;
	
	@Autowired
	private UserDAO userDAO;

	@RequestMapping(value= "/users/current/servicerequests", method = RequestMethod.POST)
	public ResponseEntity<?> postServiceRequest(@Valid @RequestBody ServiceRequest serviceRequest, HttpServletRequest request,
            HttpServletResponse response) {
			User user = userDAO.getUserByEmail(SecurityUtils.getCurrentLogin());
			serviceRequest.setUserId(user.getUserId());
			serviceRequestDAO.insertServiceRequest(serviceRequest);
			//TODO: Send email to administrator (in the distant future)? 	
			return new ResponseEntity<>(HttpStatus.CREATED); 
	}
	
	@RequestMapping(value= "/users/current/servicerequests/{servicerequestid}", method = RequestMethod.POST)
	public ResponseEntity<?> updateServiceRequest(@Valid @RequestBody ServiceRequest serviceRequest, @PathVariable Integer servicerequestid) {
		User user = userDAO.getUserByEmail(SecurityUtils.getCurrentLogin());
		try {			
			serviceRequestDAO.updateServiceRequest(serviceRequest, user.getUserId());
		} catch (DataAccessException e) {
			log.debug("Failed to update service request with Id: '{}' for user '{}'. Exception is: {} ", serviceRequest.getServiceRequestId(), SecurityUtils.getCurrentLogin(), e.getLocalizedMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}			
		log.debug("Successfuly updated service request with Id: '{}' for user '{}'. ", serviceRequest.getServiceRequestId(), SecurityUtils.getCurrentLogin());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping("/users/current/servicerequests")
	public List<ServiceRequest> servicerequests() {
		User user = userDAO.getUserByEmail(SecurityUtils.getCurrentLogin());
		return serviceRequestDAO.getServiceRequestsForUser(user.getUserId());
	}
	
//	@RequestMapping("/users/current/servicerequests/{servicerequestid}")
//	public ServiceRequest servicerequest(@PathVariable Integer servicerequestid) {
//		User user = userDAO.getUserByEmail(SecurityUtils.getCurrentLogin());
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("userid", user.getUserId());
//		params.put("servicerequestid", servicerequestid);
//		return serviceRequestDAO.getServiceRequest(servicerequestid, params);
//	}
//		
//	@RequestMapping(value= "/users/current/servicerequests/{servicerequestid}", method = RequestMethod.POST)
//	public ResponseEntity<?> updateServicerequest(@RequestBody Professional professional) {
//		try {
//			int professionalId = professionalDAO.getProfessionalId(SecurityUtils.getCurrentLogin());
//			professional.setProfessionalId(professionalId);
//			try {
//				professionalDAO.updateProfessional(professional);
//			} catch (DataAccessException e) {
//				log.debug("Failed to update data for logged in professional: '{}' with Id '{}'. Exceptin is: {} ", SecurityUtils.getCurrentLogin(), professional.getProfessionalId(), e.getLocalizedMessage());
//				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//			}			
//			log.debug("Successfuly updated data for logged in professional: '{}' with Id '{}'.", SecurityUtils.getCurrentLogin(), professional.getProfessionalId());
//			return new ResponseEntity<>(HttpStatus.OK);
//		} catch (EmptyResultDataAccessException e) {
//			log.debug("Failed to resolve ID of logged in professional: '{}'. {}", SecurityUtils.getCurrentLogin());
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		} 
//	}
	
}

