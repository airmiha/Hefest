package com.hefest.app.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LocalityDAO {
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplateObject;
	
	public List<Map<String, Object>> getCounties() {
		String sql = "SELECT countyid AS value, name AS label FROM professionals.counties ORDER BY name";

	     List<Map<String, Object>> counties = jdbcTemplateObject.query(sql, new  ColumnMapRowMapper());
	     return counties;
	}

	public List<Map<String, Object>> getDistricts(String countyid) {
		 String sql = "SELECT districts.districtid, districts.name FROM districts";
		 if(!"all".equals(countyid)) {
			 sql += ", counties "
				 + "WHERE districts.countyid = '" + countyid + "'";
		 }
		 		
	     List<Map<String, Object>> districts = jdbcTemplateObject.query(sql, new ColumnMapRowMapper());
	     return districts;
	}

	public List<Map<String, String>> getLocalities(String districtid) {
		 String sql = "SELECT localities.localityid, localities.name FROM localities";
		
		 sql += " WHERE localities.districtid='" + districtid + "'";
	
	     List<Map<String, String>> localities = jdbcTemplateObject.query(sql, 
	        new RowMapper<Map<String, String>>() {
	
				@Override
				public Map<String, String> mapRow(ResultSet resultSet,
						int rowNumber) throws SQLException {
					Map<String, String> locality  = new HashMap<String, String>();
					locality.put("value", resultSet.getString("localityid"));
					locality.put("name", resultSet.getString("name"));
					return locality;
				}
	     	});
	     return localities;
	}

	public List<Map<String, Object>> getMunicipalities(Map<String, String> params) {
		String filter = params.get("filter");
		//TODO: Using "Upper" causes performance problems. Switch this to collation.
		Map<String, String> filters = new HashMap<String, String>();
		filters.put("filter", filter + "%");
		String sql = 
				"SELECT name, path, localityid as id, countyid, 'Localities' as source FROM professionals.localities"
				+ " WHERE UPPER(name) LIKE UPPER(:filter)" 
				+ " UNION ALL"
				+ " SELECT name, path, districtid as id, countyid, 'Districts' as source FROM professionals.districts"
				+ " WHERE UPPER(name) LIKE UPPER(:filter)"
				+ " UNION ALL" 
				+ " SELECT name, path, countyid as id, countyid, 'Counties' as source FROM professionals.counties WHERE UPPER(name) LIKE UPPER(:filter)"
				+ " LIMIT 10";
		
		 List<Map<String, Object>> municipalities = jdbcTemplateObject.query(sql, filters, new ColumnMapRowMapper());		  
		 return municipalities;
	}

	public List<Map<String, Object>> getLocalities(Map<String, String> params) {
		String filter = params.get("filter");
		//TODO: Using "Upper" causes performance problems. Switch this to collation.
		Map<String, String> filters = new HashMap<String, String>();
		filters.put("filter", filter + "%");
		String sql = 
				"SELECT name, path, localityid as id FROM professionals.localities"
				+ " WHERE UPPER(name) LIKE UPPER(:filter)" 				
				+ " LIMIT 10";
		
		 List<Map<String, Object>> localities = jdbcTemplateObject.query(sql, filters, new ColumnMapRowMapper());		  
		 return localities;
	}
}
