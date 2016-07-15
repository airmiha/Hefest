package com.hefest.app.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hefest.app.model.Image;
import com.hefest.app.model.ServiceRequest;

@Component
public class ServiceRequestDAO {
	
    @SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(ServiceRequestDAO.class);
    	
	private NamedParameterJdbcTemplate jdbcTemplateObject;
	
	private SimpleJdbcInsert insertServiceRequest;
	
	@Autowired
	public ServiceRequestDAO(NamedParameterJdbcTemplate jdbcTemplateObject, DataSource dataSource) {
		this.jdbcTemplateObject = jdbcTemplateObject;
		this.insertServiceRequest = new SimpleJdbcInsert(dataSource).withTableName("professionals.servicerequests").usingGeneratedKeyColumns("servicerequestid");
	}

	public ServiceRequest insertServiceRequest(ServiceRequest serviceRequest) {
		//TODO: java.sql.Date usage is strongly discouraged in Jackson's documentation because it has timezone issues. 
		//However, for service requests we initially need only an approximate date so I'll leave this to be revisited in the future.
		serviceRequest.setRequestDate(new java.sql.Date(new java.util.Date().getTime()));
		Number serviceRequestId = insertServiceRequest.executeAndReturnKey(new BeanPropertySqlParameterSource(serviceRequest)); 		
		serviceRequest.setServiceRequestId(serviceRequestId.intValue()); 
		return serviceRequest;
	}
	
	@Transactional
	public void updateServiceRequest(ServiceRequest serviceRequest, int userId) {
		String SQL = "";
		serviceRequest.setUserId(userId);
		jdbcTemplateObject.update(SQL, new BeanPropertySqlParameterSource(serviceRequest));
		List<Integer> items = serviceRequest.getItemIds();
		String deleteItemsSQL = "DELETE FROM professionals.itemsforrequests WHERE itemsforrequests.servicerequestid=:servicerequestId AND itemsforrequests.userid=:userId";
		jdbcTemplateObject.update(deleteItemsSQL, new BeanPropertySqlParameterSource(serviceRequest));
		String updateItemsSQL = "INSERT INTO professionals.itemsforrequests(servicerequestid, itemid) VALUES(:servicerequestId, :itemid)";
		List<Map<String, Object>> updateParams = new ArrayList<Map<String, Object>>();
		for (int itemid : items) {
			Map<String, Object> rowParams = new HashMap<String, Object>();
			rowParams.put("servicerequestid", serviceRequest.getServiceRequestId());
			rowParams.put("itemid", itemid);
			updateParams.add(rowParams);
		}
		@SuppressWarnings("unchecked")
		HashMap<String, Object>[] mapAsArray = new HashMap[updateParams.size()];
		mapAsArray = updateParams.toArray(mapAsArray);
		jdbcTemplateObject.batchUpdate(updateItemsSQL, mapAsArray);
	}
	
	public List<ServiceRequest> getServiceRequestsForUser(int userId) {
		String sql = "SELECT professionals.servicerequests.*  FROM professionals.servicerequests WHERE servicerequests.userid =:userid ORDER BY servicerequestid";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userid", userId);
	    List<ServiceRequest> serviceRequests = jdbcTemplateObject.query(sql, params, new BeanPropertyRowMapper<ServiceRequest>(ServiceRequest.class));
	    return serviceRequests;	
	}

	@Transactional
	public ServiceRequest getServiceRequestForUser(int serviceRequestId, int userId) {
		String sql = "SELECT professionals.servicerequests.* FROM professionals.servicerequests WHERE servicerequests.servicerequestid =:servicerequestid"
				+ " AND servicerequests.userid =:userid ORDER BY servicerequestid";
		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("servicerequestid", serviceRequestId);
		params.put("userid", userId);
		ServiceRequest serviceRequest;
		try {
	    	serviceRequest = jdbcTemplateObject.queryForObject(sql, params, new BeanPropertyRowMapper<ServiceRequest>(ServiceRequest.class));
	    	String itemsSQL = "SELECT itemsforrequests.itemid FROM professionals.itemsforrequests"	    				  
		    		 		+ " WHERE itemsforrequests.servicerequestid =:servicerequestid";
			List<Integer> itemIds = jdbcTemplateObject.queryForList(itemsSQL, params, Integer.class);
			serviceRequest.setItemIds(itemIds);
			String imagesSQL = "SELECT imagesforservicerequests.* FROM professionals.imagesforservicerequests"	    				  
    		 		+ " WHERE imagesforservicerequests.servicerequestid =:servicerequestid";
			List<Image> images = jdbcTemplateObject.queryForList(imagesSQL, params, Image.class);
			serviceRequest.setImages(images);
	    	return serviceRequest;
	     } catch (DataAccessException e) {
	    	return null; 
	     } 
	}
	
//	public List<ServiceRequest> getServiceRequestsForProfessional(int professionalId) {
//		String sql = "SELECT professionals.servicerequests.*  FROM professionals.servicerequests WHERE servicerequests.userid =:userid ORDER BY servicerequestid";
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("userid", professionalId);
//	    List<ServiceRequest> serviceRequests = jdbcTemplateObject.query (sql, params, new BeanPropertyRowMapper<ServiceRequest>(ServiceRequest.class));
//	    return serviceRequests;	
//	}
//
//	public ServiceRequest getServiceRequestForProfessional(int servicerequestid, int professionalId) {
//		String sql = "SELECT professionals.servicerequests.* FROM professionals.servicerequests WHERE servicerequests.servicerequestid =:servicerequestid"
//				+ " AND servicerequests.userid =:userid ORDER BY servicerequestid";
//		Map<String, Object> params = new HashMap<String, Object>(); 
//		params.put("servicerequestid", servicerequestid);
//		params.put("userid", userId);
//		try {
//	    	return (ServiceRequest) jdbcTemplateObject.queryForObject(sql, params, new BeanPropertyRowMapper<ServiceRequest>(ServiceRequest.class));
//	     } catch (EmptyResultDataAccessException e) {
//	    	return null; 
//	     }
//	}
}