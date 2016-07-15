package com.hefest.app.resource;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hefest.app.dao.LocalityDAO;

@RestController
@RequestMapping("/resources")
public class LocalityResource {

	@Autowired
	private LocalityDAO localityDAO;
		
	@RequestMapping("/counties")
	@Timed
	public List<Map<String, Object>> counties() {
		return localityDAO.getCounties();
	}
	
	@RequestMapping("/municipalities")
	@Timed
	public List<Map<String, Object>> municipalities(@RequestParam Map<String, String> params) {
		return localityDAO.getMunicipalities(params);
	}
	
	@RequestMapping("/localities")
	@Timed
	public List<Map<String, Object>> localities(@RequestParam Map<String, String> params) {
		return localityDAO.getLocalities(params);
	}
}
