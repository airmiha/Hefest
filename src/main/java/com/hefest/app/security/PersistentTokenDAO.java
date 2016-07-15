package com.hefest.app.security;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PersistentTokenDAO  {

	private NamedParameterJdbcTemplate jdbcTemplateObject;
	private SimpleJdbcInsert insertToken;
	
	@Autowired
	public PersistentTokenDAO(NamedParameterJdbcTemplate jdbcTemplateObject, DataSource dataSource) {
	      this.jdbcTemplateObject = jdbcTemplateObject;
	      this.insertToken = new SimpleJdbcInsert(dataSource).withTableName("professionals.persistenttokens").usingColumns("series","email","value", "date","ipaddress","useragent");
	}

	public PersistentToken getTokenBySeries(String presentedSeries) {
		 String SQL = "SELECT professionals.persistenttokens.*  FROM professionals.persistenttokens WHERE persistenttokens.series=:series";
		 Map<String, Object> paramMap = new HashMap<String, Object>();
	     paramMap.put("series", presentedSeries);	
	     try {
	    	return (PersistentToken) jdbcTemplateObject.queryForObject(SQL, paramMap, new BeanPropertyRowMapper<PersistentToken>(PersistentToken.class));
	     } catch (EmptyResultDataAccessException e) {
	    	return null; 
	     }
	}

	public void delete(PersistentToken token) {
		 String SQL = "DELETE FROM professionals.persistenttokens WHERE persistenttokens.series=:series";
		 Map<String, Object> paramMap = new HashMap<String, Object>();
	     paramMap.put("series", token.getSeries());	
	     jdbcTemplateObject.update(SQL, paramMap);
	    
	}

	public void update(PersistentToken token) {
		String SQL = "UPDATE professionals.persistenttokens SET value=:value, date=:date, ipaddress=:ipAddress, useragent=:userAgent WHERE persistenttokens.series=:series";
		jdbcTemplateObject.update(SQL, new BeanPropertySqlParameterSource(token));
	}

	public void insert(PersistentToken token) {
		insertToken.execute(new BeanPropertySqlParameterSource(token));		
	}
	
   /**
    * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
    * 30 days.
    * <p/>
    * <p>
    * This is scheduled to get fired everyday, at midnight.
    * </p>
    */
   @Scheduled(cron = "0 0 0 * * ?")
   public void removeOldPersistentTokens() {
       LocalDate now = new LocalDate();
       deleteWhereTokenDateBefore(now.minusMonths(1));
   }
   
	private void deleteWhereTokenDateBefore(LocalDate localDate) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
		String formattedDate = formatter.print(localDate);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", formattedDate);
		String SQL = "DELETE FROM professionals.persistenttokens WHERE date>:date";
		jdbcTemplateObject.update(SQL, params);
	}
}
