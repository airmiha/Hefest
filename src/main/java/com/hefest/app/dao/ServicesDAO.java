package com.hefest.app.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ServicesDAO {
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplateObject;
	
	public Map<Integer, Map<String, Object>> getProfessionsAndCategories() {
		final Map<Integer, Map<String, Object>> professions = new HashMap<Integer, Map<String,Object>>();		
	     jdbcTemplateObject.query(getProfessionsTreeSQL(), new ProfessionTreeRowMapper(professions));
	     return professions;
	 }
	
	public List<Map<String, Object>> getItems(List<String> tags) {
		String selectItemsForTagSql = "SELECT i.* FROM professionals.tagsforitems ti, professionals.items i, professionals.tags t WHERE ti.tagid = t.tagid AND (t.tagname LIKE";
		String sql = selectItemsForTagSql;
		Map<String, String> properties = new HashMap<String, String>();
		String tagsAsString = ""; 
		for(int i = 0; i< tags.size(); i++) {
			sql += " :tag" + i + ") AND i.itemid = ti.itemid GROUP BY i.itemid";
			properties.put("tag" + i, tags.get(i).toLowerCase() + "%");
			tagsAsString += tags.get(i);
			if (i < tags.size() - 1) {
				sql += " INTERSECT " + selectItemsForTagSql;
				tagsAsString += " ";
			}
		}
		
		sql += " UNION ALL SELECT professionals.professionalid as itemid, 'professionals' as categorytype, users.name, users.name AS fullname FROM professionals.professionals"
			   + " JOIN professionals.users ON professionals.userid=users.userid WHERE UPPER(users.name) LIKE UPPER(:filter) OR UPPER(professionals.ownername) LIKE UPPER(:filter)";
		
		sql += " LIMIT 10";
		
		properties.put("filter", tagsAsString + "%");
		
		List<Map<String, Object>> items = jdbcTemplateObject.query(sql, properties, new ColumnMapRowMapper());
		return items;
	}

	public List<Map<String, Object>> getProfessions() {
		String sql = "SELECT name, professionid AS id, professions.itemid FROM professionals.professions"
				+ " JOIN professionals.items ON professions.itemid=items.itemid"
				+ " ORDER BY 1;";
		List<Map<String, Object>> professions = jdbcTemplateObject.query(sql, new ColumnMapRowMapper());
		return professions;
	}
	
	public List<Map<String, Object>> getServiceCategories(Integer professionId) {
		String sql = "SELECT name, servicecategoryid AS id, servicecategories.itemid FROM professionals.servicecategories"
				+ " JOIN professionals.items ON servicecategories.itemid=items.itemid"
				+ " WHERE servicecategories.professionid=:professionid"
				+ " ORDER BY 1";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("professionid", professionId);		
		List<Map<String, Object>> serviceCategories = jdbcTemplateObject.query(sql, parameters, new ColumnMapRowMapper());
		return serviceCategories;
	}

	public Map<String, Object> getServiceCategory(Integer servicecategoryid) {
		String sql = "SELECT servicecategories.*, items.* FROM professionals.servicecategories"
				+ " JOIN professionals.items ON servicecategories.itemid = items.itemid"
				+ " WHERE servicecategories.itemid=:id;";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", servicecategoryid);
		Map<String, Object> result = jdbcTemplateObject.queryForMap(sql, parameters); 
		return result;
	}
	
	private String getProfessionsTreeSQL() {
		return "SELECT NULL as serviceid, NULL as servicename, servicecategories.itemid AS servicecategoryid, sci.name AS servicecategoryname, professions.itemid AS professionid, pi.name AS professionname"
				+ " FROM professionals.servicecategories JOIN professionals.items AS sci ON servicecategories.itemid=sci.itemid"
				+ " JOIN professionals.professions ON servicecategories.professionid=professions.professionid"
				+ " JOIN professionals.items AS pi ON professions.itemid=pi.itemid"
				+ " UNION ALL"
				+ " SELECT services.itemid as serviceid, si.name as servicename, servicecategories.itemid AS servicecategoryid, sci.name AS servicecategoryname, professions.itemid AS professionid, pi.name AS professionname"
				+ " FROM professionals.services JOIN professionals.items AS si ON services.itemid=si.itemid"
				+ " JOIN professionals.servicecategories ON services.servicecategoryid = servicecategories.servicecategoryid"
				+ " JOIN professionals.items AS sci ON servicecategories.itemid=sci.itemid"
				+ " JOIN professionals.professions ON servicecategories.professionid=professions.professionid"
				+ " JOIN professionals.items AS pi ON professions.itemid=pi.itemid"  
				+ " ORDER BY 6";
	}

}
