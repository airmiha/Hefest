package com.hefest.app.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class Utility {
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplateObject;

	public int getGroupByBitCount(Map<String, String> parameters) {
//	    double minLat;
//	    double minLong;
//	    double maxLat;
//	    double maxLong;

//		try {
//			minLat = Double.parseDouble(parameters.get("minLat"));
//		    minLong = Double.parseDouble(parameters.get("minLong"));
//		    maxLat = Double.parseDouble(parameters.get("maxLat"));
//		    maxLong = Double.parseDouble(parameters.get("maxLong"));
//		} catch (NumberFormatException  e) {
//			throw new IllegalArgumentException("Arguments are not doubles as expected. This is probably a bug on a client side, or is an SQL injection attempt.");
//		}    
//		String geohash1 = getGeohash(minLat, minLong); 
//	    String geohash2 = getGeohash(maxLat, maxLong);
//	    int len = Math.min(geohash1.length(), geohash2.length());
//	    for (int i = 0; i < len; i++){
//            if (geohash1.charAt(i) != geohash2.charAt(i)){
//            	return i;
//            }            
//        }
		try {
			double zoom = Double.parseDouble(parameters.get("zoom"));
			return (int) (zoom < 7 ? zoom*2 : zoom*3.4);
		} catch (NumberFormatException  e) {
			throw new IllegalArgumentException("Arguments are not doubles as expected. This is probably a bug on a client side, or is an SQL injection attempt.");
		} 
	}

	private String getGeohash(double lat, double lng){
		/* No unit test for this one, so it can stay private */
		
		String SQL = String.format(Locale.ENGLISH, "SELECT professionals.string_to_bits(ST_geohash(ST_GeomFromText('POINT(%f %f)'))) as geohash;", lat, lng);		 
	    List<Map<String, Object>> data = jdbcTemplateObject.query(SQL, 
	        new RowMapper<Map<String, Object>>() {
	
				@Override
				public Map<String, Object> mapRow(ResultSet resultSet,
						int rowNumber) throws SQLException {
					ResultSetMetaData metadata = resultSet.getMetaData();
					Map<String, Object> rowData = new HashMap<String, Object>();
					for(int i=1; i<metadata.getColumnCount()+1; i++) {				  		
				  		rowData.put(metadata.getColumnName(i), resultSet.getObject(i));
				  	}			  						  						
					return rowData;
				}
	     	});
	    String result = data.get(0).get("geohash").toString(); 
	    return result;
	
	}
	
	public String getViewPortCondition(Map<String, String> parameters) {
		String point1 = parameters.get("minLong") + " " + parameters.get("minLat");
		String point2 = parameters.get("minLong") + " " + parameters.get("maxLat");
		String point3 = parameters.get("maxLong") + " " + parameters.get("maxLat");
		String point4 = parameters.get("maxLong") + " " + parameters.get("minLat");
		
		try {
			String [] params = {"minLong", "maxLong","maxLong", "minLong"};
			for (String param : params){
				Double.parseDouble(parameters.get(param));					
			}			
		} catch (NumberFormatException  e) {
			throw new IllegalArgumentException("Arguments are not doubles as expected. This is probably a bug on a client side, or is an SQL injection attempt.");
		}		
		
		return String.format("lat > :minLat AND lat < :maxLat AND lon > :minLon AND lon < :maxLon ", point1, point2, point3, point4, point1);
	}
}
