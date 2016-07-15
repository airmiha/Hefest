package com.hefest.app.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

public class ProfessionTreeRowMapper implements RowMapper<Object>{

	private Map<Integer, Map<String, Object>> professions;

	public ProfessionTreeRowMapper( Map<Integer, Map<String, Object>> professions) {
		this.professions = professions;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		Integer professionId = resultSet.getInt("professionid");					
		Integer serviceCategoryId = resultSet.getInt("servicecategoryid");
		Integer serviceId = resultSet.getInt("serviceid");
		String serviceName = resultSet.getString("servicename");
		Map<Integer, Map<String, Object>> serviceCategories;
		if(!professions.containsKey(professionId)) {
			Map<String, Object> profession = new HashMap<String, Object>();
			serviceCategories = new HashMap<Integer, Map<String, Object>>();						
			profession.put("name", resultSet.getString("professionname"));
			profession.put("serviceCategories", serviceCategories);
			professions.put(professionId, profession);
		} else {
			Map<String, Object> profession = professions.get(professionId);
			serviceCategories = (Map<Integer, Map<String, Object>>) profession.get("serviceCategories");					
		}
		Map<Integer, String> services;
		if(!serviceCategories.containsKey(serviceCategoryId)) {
			Map<String, Object> serviceCategory = new HashMap<String, Object>();
			services = new HashMap<Integer, String>();						
			serviceCategory.put("name", resultSet.getString("servicecategoryname"));
			try {
				serviceCategory.put("description", resultSet.getString("servicecategorydescription"));
			} catch (Exception e) {
				
			}
			serviceCategory.put("services", services);
			serviceCategories.put(serviceCategoryId, serviceCategory);
		} else {
			Map<String, Object> serviceCategory = serviceCategories.get(serviceCategoryId);
			services = (Map<Integer, String>) serviceCategory.get("services");					
		}
		if(serviceId != null && serviceName != null) {
			services.put(serviceId, serviceName);
		}
	  							
		return null;
	}

}
