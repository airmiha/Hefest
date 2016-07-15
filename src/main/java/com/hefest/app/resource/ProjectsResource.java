package com.hefest.app.resource;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hefest.app.dao.ProjectDAO;
import com.hefest.app.dao.ServicesDAO;
import com.hefest.app.dao.UserDAO;
import com.hefest.app.model.MultiResponse;
import com.hefest.app.model.Project;

@RestController
@RequestMapping("/resources")
public class ProjectsResource {
	
	private final Logger log = LoggerFactory.getLogger(ProjectsResource.class);
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private ServicesDAO servicesDAO;
	
	@Autowired
	private ProjectDAO projectDAO;
	
	@RequestMapping("/projects")
	@Timed
	public MultiResponse<String> projects(@RequestParam(value="tags", required = false) List<String> tags, @RequestParam Map<String, String> params) {
		return projectDAO.getProjects(tags, params);
	}
	
	@RequestMapping("/projects/{projectid}")
	@Timed
	public Project project(@PathVariable Integer projectid) {
		return projectDAO.getProject(projectid);
	}
	

}
