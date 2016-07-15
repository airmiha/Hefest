package com.hefest.app.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hefest.app.model.Feature;
import com.hefest.app.model.Image;
import com.hefest.app.model.MultiResponse;
import com.hefest.app.model.Professional;
import com.hefest.app.model.Response;
import com.hefest.app.model.Testimonial;
import com.hefest.app.model.User;
import com.hefest.app.utility.Utility;

@Component
public class ProfessionalDAO {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private Utility geoUtility;
	
	@Autowired
	private UserDAO userDAO;
	
	private NamedParameterJdbcTemplate jdbcTemplateObject;
	
	private Set<String> sortColumns = new HashSet<String>(Arrays.asList("name", "score", "avgreview", "projectcount", "endorsementcount"));
	
	private SimpleJdbcInsert insertProfessional;

	@Autowired
	public ProfessionalDAO(NamedParameterJdbcTemplate jdbcTemplateObject, DataSource dataSource) {
	      this.jdbcTemplateObject = jdbcTemplateObject;
	      this.insertProfessional = new SimpleJdbcInsert(dataSource).withTableName("professionals.professionals").usingColumns("userid").usingGeneratedKeyColumns("professionalid");
	}
	
	public MultiResponse<String> getProfessionals(List<String> tags, Map<String, String> parameters) {
	 	
		 Map<String, Object> properties = new HashMap<String, Object>();
		 
		 List<String> whereConditions = new ArrayList<String>();
		 
		 String orderBy = "";
		 String limit = "";
		 String offset = "";
		 
		 String SQL;
		 long time1 = (new Date()).getTime();
		 
		 final boolean isForMap = !parameters.containsKey("limit");
		 
		 SQL = getSelectForList();	
		 if(isForMap) {				
			int groupByBits = geoUtility.getGroupByBitCount(parameters);
			String whereInViewPortCondition = " addresses.lat > :minLat AND addresses.lat < :maxLat AND addresses.long > :minLong AND addresses.long < :maxLong"; 
			whereConditions.add(whereInViewPortCondition);
			properties.put("minLat", Double.parseDouble(parameters.get("minLat")));
			properties.put("maxLat", Double.parseDouble(parameters.get("maxLat")));
			properties.put("minLong", Double.parseDouble(parameters.get("minLong")));
			properties.put("maxLong", Double.parseDouble(parameters.get("maxLong")));
		 } else {
			
		 }
		 
		 SQL += " FROM professionals.professionals" 
					+ " JOIN professionals.users ON users.userid=professionals.userid" 
					+ " LEFT JOIN professionals.addresses ON users.addressid=addresses.addressid"; 
		 
		 for(Map.Entry<String, String> entry : parameters.entrySet()) {
			 String key = entry.getKey();
		 	if(("professionalid").equals(key)) {
		 		whereConditions.add(" professionalid=:professionalid");
		 		properties.put("professionalid", Integer.valueOf(parameters.get("professionalid")));
		 	} else if(("itemid").equals(key)) {
		 		SQL += " LEFT JOIN professionals.itemsforprofessionals as i ON professionals.professionalid=i.professionalid";
		 		whereConditions.add(" i.itemid=:itemid");
		 		properties.put("itemid", Integer.valueOf(parameters.get("itemid")));
		 	} else if(("countyid").equals(key)) {
		 		SQL += " LEFT JOIN professionals.professionalsforcounties as c ON professionals.professionalid=c.professionalid";
		 		whereConditions.add(" c.countyid=:countyid");
		 		properties.put("countyid", Integer.valueOf(parameters.get("countyid")));
			 } else if(("municipality").equals(key) && !parameters.containsKey("countyid")) {
		 		SQL += " LEFT JOIN professionals.professionalsforcounties as c ON professionals.professionalid=c.professionalid"
		 			 + " LEFT JOIN professionals.counties ON c.countyid=counties.countyid"
		 			 + " INNER JOIN  ("
		 			 + " SELECT countyid FROM professionals.counties WHERE UPPER(counties.name)=:municipality"
		 			 + " UNION SELECT countyid FROM professionals.districts WHERE UPPER(districts.name)=:municipality"
		 			 + " UNION SELECT countyid FROM professionals.localities WHERE UPPER(localities.name)=:municipality) d"
		 			 + " ON c.countyid=d.countyid";
		 		properties.put("municipality", parameters.get("municipality").toUpperCase());
			}  else if(("orderbycolumn").equals(key)) {
	 			orderBy += " ORDER BY ";
	 			String orderByColumn = sortColumns.contains(parameters.get("orderbycolumn")) ? parameters.get("orderbycolumn") : "name";
	 			orderBy	+= orderByColumn;	 	
		 		String order = "ASC".equals(parameters.get("order")) ? " ASC" : " DESC";	
		 		orderBy += order;
			} else if("limit".equals(key)) {
				try {
					limit = " LIMIT " + Integer.parseInt(parameters.get("limit"));										
				} catch (Exception e) {
					//Log or do nothing
				}
			} else if("offset".equals(key)){
				try {
					offset = " OFFSET " + Integer.parseInt(parameters.get("offset"));										
				} catch (Exception e) {
					//Log - possible SQL injection attempt
				}
			} else if ("minEmployees".equals(key)) {
				try {
					int minEmployees = Integer.parseInt(entry.getValue());
					whereConditions.add(" employeecount >= :minEmployees");
					properties.put("minEmployees", minEmployees);								
				} catch (Exception e) {
					//Log - possible SQL injection attempt
				}				
			} else if ("minYearsinBusiness".equals(key)) {
				try {
					int year = Calendar.getInstance().get(Calendar.YEAR);
					int yearEstablished = year - Integer.parseInt(entry.getValue());
					properties.put("yearestablished", yearEstablished);
					whereConditions.add(" yearestablished <= :yearestablished");									
				} catch (Exception e) {
					//Log - possible SQL injection attempt
				}
			} else if ("acceptscreditcards".equals(key) && "true".equals(entry.getValue())) {
				whereConditions.add(" acceptscreditcards = true");
			}
		 }
		 	 
		 if (tags != null && !tags.isEmpty()) {
			 SQL += " LEFT JOIN professionals.itemsforprofessionals as i ON professionals.professionalid=i.professionalid"
				+ " LEFT JOIN professionals.tagsforitems as t ON i.itemid = t.itemid"
				+ " LEFT JOIN professionals.tags ON t.tagid=tags.tagid"; 
			 
			String whereCondition = "("; 
			for(int i = 0; i< tags.size(); i++) {
				whereCondition += "tags.tagname LIKE :tag" + i;
				properties.put("tag" + i, tags.get(i).toLowerCase() + "%");
				if (i < tags.size() - 1) {
					whereCondition += " OR ";
				}
			}
			whereConditions.add(whereCondition + ")");
		 }
		 
		 if(!whereConditions.isEmpty()) {
			SQL += " WHERE " + StringUtils.collectionToDelimitedString(whereConditions, " AND "); 			
		 }
		 
		 SQL += orderBy;
		 SQL += limit;
		 SQL += offset + ";";
					 						
		 final Map<String, Integer> nameToIndexMap = new HashMap<>();		 	
		 final List<Object> responseMetadata = new ArrayList<Object>();
		 final List<List<String>> clusterList = new LinkedList<List<String>>();
		 final List<List<String>> professionalsList = new LinkedList<List<String>>();		 
		 final MutableDouble maxLat = new MutableDouble();
		 final MutableDouble minLat = new MutableDouble();
		 final MutableDouble maxLng = new MutableDouble();
		 final MutableDouble minLng = new MutableDouble();
		 
		 clusterList.clear();
		 professionalsList.clear();
		
		 final String [] clusterMetadata = {"lat", "long", "count"};
		 final String [] professionalsMetadata = {"name", "professions","leadimage","addressline", "lat", "long", "professionalid","score","endorsementcount","projectcount","reviewcount","avgreview"};		 
		 final String [] viewPortMetadata = {"minLat", "minLng", "maxLat", "maxLng"}; 
		 
		 final List<Integer> totalList = new ArrayList<Integer>();
		 
		 jdbcTemplateObject.query(SQL, properties, 
            new RowMapper<List<Object>>() {

				@Override
				public List<Object> mapRow(ResultSet resultSet,
						int rowNumber) throws SQLException {
					ResultSetMetaData metadata = resultSet.getMetaData();
					if(!isForMap && totalList.isEmpty()) {
						totalList.add(resultSet.getInt("total"));
					}
					
					List<Object> rowData = new ArrayList<Object>();
					if(responseMetadata.isEmpty()) {
						for(int i=1; i<metadata.getColumnCount()+1; i++) {	
					  		String columnName = metadata.getColumnName(i);
							responseMetadata.add(columnName);
					  		nameToIndexMap.put(columnName, i-1);
					  	}			
					}
					for(int i=1; i<metadata.getColumnCount()+1; i++) {
				  		rowData.add(resultSet.getObject(i));
				  	}			  		
			
					// get data for viewPort
//					minLng.setValue(Math.min(minLng.doubleValue(), Double.parseDouble(rowData.get(nameToIndexMap.get("long")).toString())));
//					minLat.setValue(Math.min(minLat.doubleValue(), Double.parseDouble(rowData.get(nameToIndexMap.get("lat")).toString())));
//					maxLng.setValue(Math.max(maxLng.doubleValue(), Double.parseDouble(rowData.get(nameToIndexMap.get("long")).toString())));
//					maxLat.setValue(Math.max(maxLat.doubleValue(), Double.parseDouble(rowData.get(nameToIndexMap.get("lat")).toString())));
					
					List<String> tmpList = new LinkedList<String>();											
					if (nameToIndexMap.containsKey("count") && (long) rowData.get(nameToIndexMap.get("count")) > 1 && isForMap){					
						// this is a cluster											
						for (String value : clusterMetadata){
							try {							
								tmpList.add(rowData.get(nameToIndexMap.get(value)).toString());
							} catch (Exception e) {
								// Add logging... 						
							}
						} 																		
						clusterList.add(tmpList);

					} else {
						// this is single professional		
						for (String value : professionalsMetadata){							
							Object rv = rowData.get(nameToIndexMap.get(value));
							if (rv == null){								
								rv = "";
							}													
							tmpList.add(rv.toString());
						} 																								
						professionalsList.add(tmpList);
					}
					return rowData;
				}
		}); 
	    
	    long time2 = (new Date()).getTime();
	    // used for debugging
	    System.out.println("Total request time: " + Long.toString(time2-time1));
	    
	    // TODO: create response with three lists of lists of lists :)	    
	    // data = new LinkedList<List<Object>>();
	    // data.add(professionalList);
	    
	    List<List<String>> metadataList = new LinkedList<List<String>>();
	    metadataList.add(Arrays.asList(clusterMetadata));
	    metadataList.add(Arrays.asList(professionalsMetadata));
	    metadataList.add(Arrays.asList(viewPortMetadata));
	    	    
	    List<List<List<String>>> finalData = new LinkedList<List<List<String>>>();
	    finalData.add(clusterList);
	    finalData.add(professionalsList);
	    List<String> latlongList = new LinkedList<String>(); // we could have problems with locale here...
	    latlongList.add(minLat.toString());
	    latlongList.add(minLng.toString());
	    latlongList.add(maxLat.toString());
	    latlongList.add(maxLng.toString());
	    List<List<String>> finalLatLongList = new LinkedList<List<String>>();
	    finalLatLongList.add(latlongList);
        // I really do not like how this is written... maybe there is a smarter way, without 
	    // reverting to List<Object> which is just evil in static typed language as Java
	    // but I cannot think of it right now
	    finalData.add(finalLatLongList);  
	    
	    Integer total = professionalsList.isEmpty() || totalList.isEmpty() ? 0 : totalList.get(0);
	    
	    return new MultiResponse<String>(metadataList, finalData, total);
	 }

	public int getProfessionalId(String email) {
		 String SQL = "SELECT professionals.professionalid"
				 	+ " FROM professionals.professionals" 
				 	+ " JOIN professionals.users ON users.userid=professionals.userid"
				 	+ " WHERE users.email=:email";
		 Map<String, Object> paramMap = new HashMap<String, Object>();
	     paramMap.put("email", email);	
	     return jdbcTemplateObject.queryForObject(SQL, paramMap, Integer.class);
	     
	}
	
	@Transactional
	public Professional getProfessional(Integer professionalid) {
		 String SQL = "SELECT users.name, leadimage AS leadImage, backgroundimage AS backgroundImage, mobile, telephone, professionalid AS professionalId, professionals.userid AS userId, ownerName AS ownerName,"
		 		+ " professions, pagetitle AS pageTitle, pageurl AS pageUrl, metadescription AS metaDescription, profileheadline AS profileHeadline, profilesubheadline AS profileSubHeadline, featuresheadline AS featuresHeadline, featuressubheadline AS featuresSubHeadline, profilemoneyshot AS profileMoneyShot,"
		 		+ " employeecount AS employeeCount, score, yearestablished AS yearEstablished, contactemail AS contactEmail, fax, website," 
		 		+ " projectcount AS projectCount, endorsementcount AS endorsementCount, description, reviewcount AS reviewCount, avgreview AS avgReview," 
		 		+ " verified, signupdate AS signupDate, addressline AS addressLine, lat AS latitude, long AS longitude" 
				 + " FROM professionals.professionals" 
				 + " JOIN professionals.users ON users.userid=professionals.userid" 
				 + " LEFT JOIN professionals.addresses ON users.addressid=addresses.addressid"
				 + " WHERE professionals.professionalid=:professionalid";
		 Map<String, Object> paramMap = new HashMap<String, Object>();
	     paramMap.put("professionalid", professionalid);	
	     Professional professional = jdbcTemplateObject.queryForObject(SQL, paramMap, new BeanPropertyRowMapper<Professional>(Professional.class));
	     
	     String countiesSQL = "SELECT professionalsforcounties.countyid FROM professionals.professionalsforcounties";
	     countiesSQL += " JOIN professionals.counties ON counties.countyid=professionalsforcounties.countyid"  
	    		 	+ " WHERE professionalsforcounties.professionalid=:professionalid";
		 List<Integer> countyIds = jdbcTemplateObject.queryForList(countiesSQL, paramMap, Integer.class);
		 String imagesSQL = "SELECT imagesforprofessionals.imageid AS imageId, path, description FROM professionals.imagesforprofessionals"
				 	+ " WHERE imagesforprofessionals.professionalid=:professionalid";
		 List<Image> images = jdbcTemplateObject.query(imagesSQL, paramMap, new BeanPropertyRowMapper<Image>(Image.class));
	     String selectedTestimonialsSQL = "SELECT testimonials.imagepath AS imagePath, summary, text, personname AS personName, personcompany AS personCompany FROM professionals.testimonials"
				 + " WHERE professionalid=:professionalid AND isselected=true";
		 List<Testimonial> selectedTestimonials = jdbcTemplateObject.query(selectedTestimonialsSQL, paramMap, new BeanPropertyRowMapper<Testimonial>(Testimonial.class));
	     String featuresSQL = "SELECT features.icon, headline, description FROM professionals.features"
					 + " WHERE professionalid=:professionalid";
		 List<Feature> features = jdbcTemplateObject.query(featuresSQL, paramMap, new BeanPropertyRowMapper<Feature>(Feature.class));
		 Map<Integer, Map<String, Object>> professionsAndServices = getProfessionsForProfessional(professionalid);
		 professional.setCounties(countyIds);
		 professional.setProfessionsAndServices(professionsAndServices);
		 professional.setImages(images);
		 professional.setTestimonials(selectedTestimonials);
		 professional.setFeatures(features);
	     return professional;
	}
		
	@Transactional
	public User insertProfessional(User newUser) {
		newUser.setRole("ROLE_PROFESSIONAL");
		User professional = userDAO.insertUser(newUser);		
		Map<String, Object> professionalParameters = new HashMap<String, Object>();
		professionalParameters.put("userid", professional.getUserId());		
		insertProfessional.executeAndReturnKey (professionalParameters);  
		return professional;
	}
	
	@Transactional
	public void updateProfessional(Professional professional) {
		String SQL = "UPDATE professionals.professionals SET ownername=:ownerName, profileHeadline=:profileHeadline, profilesubheadline=:profileSubHeadline," 
					 + "  yearestablished=:yearEstablished, contactemail=:contactEmail, fax=:fax, website=:website, description=:description";
		SQL += " WHERE professionals.professionalid=:professionalId";
		jdbcTemplateObject.update(SQL, new BeanPropertySqlParameterSource(professional));
		SQL = "UPDATE professionals.users SET name=:name, telephone=:telephone, mobile=:mobile";
		SQL += " WHERE users.userid=(SELECT userid FROM professionals.professionals WHERE professionals.professionalid=:professionalId)";
		jdbcTemplateObject.update(SQL, new BeanPropertySqlParameterSource(professional));
		SQL = "UPDATE professionals.addresses SET addressline=:addressLine, lat=:latitude, long=:longitude";
		SQL += " WHERE addresses.addressid=(SELECT addressid FROM professionals.users WHERE users.userid=:userId)";
		jdbcTemplateObject.update(SQL, new BeanPropertySqlParameterSource(professional));
		
		if (professional.getCounties() != null) {
			List<Integer> counties = professional.getCounties();
			String deleteCountiesSQL = "DELETE FROM professionals.professionalsforcounties WHERE professionalsforcounties.professionalid=:professionalId";
			jdbcTemplateObject.update(deleteCountiesSQL, new BeanPropertySqlParameterSource(professional));
			String updateCountiesSQL = "INSERT INTO professionals.professionalsforcounties(professionalid, countyid) VALUES(:professionalid, :countyid)";
			List<Map<String, Object>> updateParams = new ArrayList<Map<String, Object>>();
			for (int countyid : counties) {
				Map<String, Object> rowParams = new HashMap<String, Object>();
				rowParams.put("professionalid", professional.getProfessionalId());
				rowParams.put("countyid", countyid);
				updateParams.add(rowParams);
			}
			@SuppressWarnings("unchecked")
			HashMap<String, Object>[] mapAsArray = new HashMap[updateParams.size()];
			mapAsArray = updateParams.toArray(mapAsArray);
			jdbcTemplateObject.batchUpdate(updateCountiesSQL, mapAsArray);
		}
	
		if( professional.getItems() != null) {
			List<String> items = professional.getItems();
			String deleteItemsSQL = "DELETE FROM professionals.itemsforprofessionals WHERE itemsforprofessionals.professionalid=:professionalId";
			jdbcTemplateObject.update(deleteItemsSQL,  new BeanPropertySqlParameterSource(professional));
			String updateItemsSQL = "INSERT INTO professionals.itemsforprofessionals(professionalid, itemid, professionaldescription) VALUES(:professionalid, :itemid, :professionaldescription)";
			List<Map<String, Object>> updateParams = new ArrayList<Map<String, Object>>();
			for (String itemAndDescriptionAsString : items) {
				String[] itemAndDescription = itemAndDescriptionAsString.split(",", 2);
				Map<String, Object> rowParams = new HashMap<String, Object>();
				rowParams.put("professionalid", professional.getProfessionalId());
				rowParams.put("itemid", Integer.valueOf(itemAndDescription[0]));
				rowParams.put("professionaldescription", itemAndDescription[1]);
				updateParams.add(rowParams);
			}
			@SuppressWarnings("unchecked")
			HashMap<String, Object>[] paramMapAsArray = new HashMap[updateParams.size()];
			paramMapAsArray = updateParams.toArray(paramMapAsArray);
			jdbcTemplateObject.batchUpdate(updateItemsSQL, paramMapAsArray);
		}
	}
	
	private Map<Integer, Map<String, Object>> getProfessionsForProfessional(Integer professionalid) {
		final Map<Integer, Map<String, Object>> professions = new HashMap<Integer, Map<String,Object>>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
	    paramMap.put("professionalid", professionalid);	
	    jdbcTemplateObject.query(getProfessionsTreeSQL(), paramMap, new ProfessionTreeRowMapper(professions));
	    return professions;
	}
	
	private String getProfessionsTreeSQL() {
		return "SELECT NULL as serviceid, NULL as servicename, servicecategories.itemid AS servicecategoryid, sci.name AS servicecategoryname, itemsforprofessionals.professionaldescription AS servicecategorydescription, professions.itemid AS professionid, pi.name AS professionname"
			  + " FROM professionals.servicecategories JOIN professionals.items AS sci ON servicecategories.itemid=sci.itemid"
			  + " JOIN professionals.itemsforprofessionals ON itemsforprofessionals.itemid=sci.itemid"
			  + " JOIN professionals.professions ON servicecategories.professionid=professions.professionid"
			  + " JOIN professionals.items AS pi ON professions.itemid=pi.itemid" 
			  + " WHERE professionalid=:professionalid"
			  + " UNION ALL"
			  + " SELECT services.itemid as serviceid, si.name as servicename, servicecategories.itemid AS servicecategoryid, sci.name AS servicecategoryname, NULL as servicecategorydescription, professions.itemid AS professionid, pi.name AS professionname"
			  + " FROM professionals.services JOIN professionals.items AS si ON services.itemid=si.itemid"
			  + " JOIN professionals.itemsforprofessionals ON itemsforprofessionals.itemid=si.itemid"
			  + " JOIN professionals.servicecategories ON services.servicecategoryid = servicecategories.servicecategoryid"
			  + " JOIN professionals.items AS sci ON servicecategories.itemid=sci.itemid"
			  + " JOIN professionals.professions ON servicecategories.professionid=professions.professionid"
			  + " JOIN professionals.items AS pi ON professions.itemid=pi.itemid"  
			  + " WHERE professionalid=:professionalid"
			  + " ORDER BY 6;";
	}
	
	private String getSelectForList() {
		 return "SELECT DISTINCT COUNT(users.userid) OVER() AS total, users.name, professions, leadimage, addressline, lat, long, professionals.professionalid, score, endorsementcount, projectcount, reviewcount, avgreview"; 
	}
	
	public Professional getBasicProfessional(Integer professionalid) {
		 String SQL = "SELECT users.name, leadimage AS leadImage, backgroundimage AS backgroundImage, mobile, telephone, professionalid AS professionalId, professionals.userid AS userId, ownerName AS ownerName,"
		 		+ " professions, pagetitle AS pageTitle, pageurl AS pageUrl, metadescription AS metaDescription, profileheadline AS profileHeadline, profilesubheadline AS profileSubHeadline, profilemoneyshot AS profileMoneyShot, valueproposition1 AS valueProposition1, valueproposition2 AS valueProposition2,"
		 		+ " valueproposition3 AS valueProposition3, employeecount AS employeeCount, score, yearestablished AS yearEstablished, contactemail AS contactEmail, fax, website," 
		 		+ " projectcount AS projectCount, endorsementcount AS endorsementCount, description, reviewcount AS reviewCount, avgreview AS avgReview," 
		 		+ " verified, signupdate AS signupDate, addressline AS addressLine, lat AS latitude, long AS longitude" 
				 + " FROM professionals.professionals" 
				 + " JOIN professionals.users ON users.userid=professionals.userid" 
				 + " LEFT JOIN professionals.addresses ON users.addressid=addresses.addressid"
				 + " WHERE professionals.professionalid=:professionalid";
		 Map<String, Object> paramMap = new HashMap<String, Object>();
	     paramMap.put("professionalid", professionalid);	
	     Professional professional = jdbcTemplateObject.queryForObject(SQL, paramMap, new BeanPropertyRowMapper<Professional>(Professional.class));
	     
	     String countiesSQL = "SELECT professionalsforcounties.countyid FROM professionals.professionalsforcounties";
	     countiesSQL += " JOIN professionals.counties ON counties.countyid=professionalsforcounties.countyid"  
	    		 	+ " WHERE professionalsforcounties.professionalid=:professionalid";
		 List<Integer> countyIds = jdbcTemplateObject.queryForList(countiesSQL, paramMap, Integer.class);
		 String imagesSQL = "SELECT imagesforprofessionals.imageid AS imageId, path, description FROM professionals.imagesforprofessionals"
				 	+ " WHERE imagesforprofessionals.professionalid=:professionalid";
		 List<Image> images = jdbcTemplateObject.query(imagesSQL, paramMap, new BeanPropertyRowMapper<Image>(Image.class));
	     String selectedTestimonialsSQL = "SELECT testimonials.imagepath AS imagePath, summary, text, personname AS personName, personcompany AS personCompany FROM professionals.testimonials"
				 + " WHERE professionalid=:professionalid AND isselected=true";
		 List<Testimonial> selectedTestimonials = jdbcTemplateObject.query(selectedTestimonialsSQL, paramMap, new BeanPropertyRowMapper<Testimonial>(Testimonial.class));
	     String featuresSQL = "SELECT features.icon, headline, description FROM professionals.features"
					 + " WHERE professionalid=:professionalid";
		 List<Feature> features = jdbcTemplateObject.query(featuresSQL, paramMap, new BeanPropertyRowMapper<Feature>(Feature.class));
		 Map<Integer, Map<String, Object>> professionsAndServices = getProfessionsForProfessional(professionalid);
		 professional.setCounties(countyIds);
		 professional.setProfessionsAndServices(professionsAndServices);
		 professional.setImages(images);
		 professional.setTestimonials(selectedTestimonials);
		 professional.setFeatures(features);
	     return professional;
	}

	public Response<Map<String, Object>> getEndorsements(Integer professionalId, Map<String, String> parameters) {			     
	     String sql = "SELECT COUNT(endorsements.endorsementid) OVER() AS total, endorsements.userid, users.name, leadimage, users.role, items.name AS profession FROM professionals.endorsements";
	     sql += " JOIN professionals.users ON endorsements.userid=users.userid"
	    	+ " JOIN professionals.professionals ON endorsements.professionalid=professionals.professionalid"
	        + " JOIN professionals.professions AS p ON endorsements.professionid=p.professionid"
	     	+ " JOIN professionals.items ON p.itemid=items.itemid"
	    	+ " WHERE endorsements.professionalid=:professionalid";
	     Map<String, Object> params = new HashMap<String, Object>(); 
	     params.put("professionalid", professionalId); 
	    
	     String role = null;
	     String limit = "";
		 String offset = "";
		
	     for(Map.Entry<String, String> entry : parameters.entrySet()) {
			 String key = entry.getKey();
			 if("limit".equals(key)) {
 				try {
 					limit = " LIMIT " + Integer.parseInt(parameters.get("limit"));										
 				} catch (Exception e) {
 					//Log or do nothing
 				}
	 		} else if("offset".equals(key)){
 				try {
 					offset = " OFFSET " + Integer.parseInt(parameters.get("offset"));										
 				} catch (Exception e) {
 					//Log - possible SQL injection attempt
 				}
	 		} else if ("role".equals(key)) {
	 			role = parameters.get("role");
	 		}
	    }
	    if (role != null) {
	    	sql += " AND role=:role";
	    	params.put("role", parameters.get("role"));
	    }
	    sql += limit;
		sql += offset + ";";
	    List<Map<String, Object>> endorsements = jdbcTemplateObject.query(sql, params, new ColumnMapRowMapper());	     
	    long total = endorsements.isEmpty() ? 0 : (long) endorsements.get(0).get("total");
	    return new Response<Map<String, Object>>(null, endorsements, (int) total);	    
	}
	
	public Response<Testimonial> getTestimonials(Integer professionalId, Map<String, String> parameters) {			     
		 String sql = "SELECT COUNT(testimonials.testimonialid) OVER() AS total, testimonials.imagepath AS imagePath, summary, text, personname AS personName, personcompany AS personCompany FROM professionals.testimonials WHERE testimonials.professionalid=:professionalid"
				 + " ORDER BY date DESC";
	     Map<String, Object> params = new HashMap<String, Object>(); 
	     params.put("professionalid", professionalId); 
	     
	     String limit = "";
		 String offset = "";
		 
	     for(Map.Entry<String, String> entry : parameters.entrySet()) {
			 String key = entry.getKey();
			 if("limit".equals(key)) {
				try {
					limit = " LIMIT " + Integer.parseInt(parameters.get("limit"));										
				} catch (Exception e) {
					//Log or do nothing
				}
	 		} else if("offset".equals(key)){
				try {
					offset = " OFFSET " + Integer.parseInt(parameters.get("offset"));										
				} catch (Exception e) {
					//Log - possible SQL injection attempt
				}
	 		} 
	    }
	  
	    sql += limit;
		sql += offset + ";";
	    List<Testimonial> testimonials = jdbcTemplateObject.query(sql, params, new BeanPropertyRowMapper<Testimonial>(Testimonial.class));	     
		int total = testimonials.isEmpty() ? 0 : testimonials.get(0).getTotal();
	    return new Response<Testimonial>(null, testimonials, total);
	}
}
