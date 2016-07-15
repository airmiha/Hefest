
package com.hefest.app.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DatabaseInitializer {
	
	public static void main(String[] args) throws SQLException {	 
		
		String inputFileDatabase = "scripts/database_create.sql";
		String inputFileProfessions = "scripts/en/professions.csv";
		String inputFileServiceCategories = "scripts/en/ServiceCategories.csv";
		String inputFileServices = "scripts/en/Services.csv";
		
		BufferedReader bufferedReader = null;
		
		try {

			// Connect to database 
			String url = "jdbc:postgresql://localhost/geoads";
			String username = "geoads";
			String password = "geoads";
			Connection conn = DriverManager.getConnection(url, username, password);

			// Connect to test database 
			String testUrl = "jdbc:postgresql://localhost/geoadstest";
			Connection testConn = DriverManager.getConnection(testUrl, "geoads", "geoads");
			
			// Reset schema		
			
			System.out.println("Creating database...");
			
			bufferedReader = new BufferedReader(new FileReader(inputFileDatabase));
			String line = "";
			StringBuffer sb = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line + "\n ");
			}
			bufferedReader.close();
			
			Statement statementDBCreate = conn.createStatement();
			statementDBCreate.executeUpdate(sb.toString());

			System.out.println("Created database");
			
			// Reset test schema		

			System.out.println("Creating test database...");
			
			bufferedReader = new BufferedReader(new FileReader(inputFileDatabase));
			line = "";
			sb = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line + "\n ");
			}
			bufferedReader.close();
			
			statementDBCreate = testConn.createStatement();
			statementDBCreate.executeUpdate(sb.toString());

			System.out.println("Created test database");

			// Import categories
			String cvsSplitBy = ",";
			String tagSplitBy = ";";

			// Professions
			
			System.out.println("Inserting professions...");
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFileProfessions)), "UTF8"));
			Map<String, Long> professions = new HashMap<String, Long>();
			String insertProfessionCommandTemplate = "INSERT INTO professionals.professions(itemid) VALUES ";
			
			while ((line = bufferedReader.readLine()) != null) {
	 			String[] professionData = line.split(cvsSplitBy);			
				String professionName = professionData[0];
				String[] professionTags = null; 
				if (professionData.length > 1 && !professionData[1].isEmpty()){
					professionTags = professionData[1].split(tagSplitBy);
				}
				//String professionDescription = professionLine[1];
				if(!professions.containsKey(professionName)) {					
					long id = insertItemWithTags(conn, professionName, professionTags, "professions");
					String insertProfessionCommand = insertProfessionCommandTemplate + "('" + id + "');";
					PreparedStatement statementInsertProfession = conn.prepareStatement(insertProfessionCommand, Statement.RETURN_GENERATED_KEYS);
					statementInsertProfession.executeUpdate();
					ResultSet generatedKeys = statementInsertProfession.getGeneratedKeys();
					generatedKeys.next();
					long professionId = generatedKeys.getLong(1);
					professions.put(professionName, professionId);					
				}
			}
			bufferedReader.close();
			
			System.out.println("Inserted professions.");
			
			// Service categories
			
			System.out.println("Inserting service categories...");
			
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFileServiceCategories)), "UTF8"));
			Map<String, Long> serviceCategories = new HashMap<String, Long>();
			String insertServiceCategoryCommandTemplate = "INSERT INTO professionals.servicecategories(itemid, professionid) VALUES ";
			while ((line = bufferedReader.readLine()) != null) {
	 			String[] serviceCategoryData = line.split(cvsSplitBy);			
	 			String professionName = serviceCategoryData[0];
				String serviceCategoryName = serviceCategoryData[1];
				String[] serviceCategoryTags = null;
				if (serviceCategoryData.length > 3 && !serviceCategoryData[3].isEmpty()){
					serviceCategoryTags = serviceCategoryData[3].split(tagSplitBy);
				}
				String serviceCategoryKey = professionName + serviceCategoryName;
				if(!serviceCategories.containsKey(serviceCategoryKey) && professions.containsKey(professionName)) {					
					long id = insertItemWithTags(conn, serviceCategoryName, serviceCategoryTags, "servicecategories");
					String insertServiceCategoryCommand = insertServiceCategoryCommandTemplate + "('" + id + "', '" + professions.get(professionName).toString() + "');";
					PreparedStatement statementInsertServiceCategory = conn.prepareStatement(insertServiceCategoryCommand, Statement.RETURN_GENERATED_KEYS);
					statementInsertServiceCategory.executeUpdate();
					ResultSet generatedKeys = statementInsertServiceCategory.getGeneratedKeys();
					generatedKeys.next();
					long serviceCategoryId = generatedKeys.getLong(1);
					serviceCategories.put(serviceCategoryKey, serviceCategoryId);
				}
				else
				{
					System.out.println("Duplicate key (" + professionName + serviceCategoryName + ") or missing profession (" + professionName + ")");
				}
			}
			bufferedReader.close();

			System.out.println("Inserted service categories...");
			
			// Services
			
			System.out.println("Inserting services...");
			
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFileServices)), "UTF8"));
			Map<String, Long> services = new HashMap<String, Long>();
			String insertServiceCommandTemplate = "INSERT INTO professionals.services(itemid, servicecategoryid) VALUES ";
			while ((line = bufferedReader.readLine()) != null) {
	 			String[] serviceData = line.split(cvsSplitBy);			
	 			String professionName = serviceData[0];
	 			String serviceCategoryName = serviceData[1];
	 			String serviceName = serviceData[2];
				String[] serviceTags = null;
				if (serviceData.length > 4 && !serviceData[4].isEmpty()){
					serviceTags = serviceData[4].split(tagSplitBy);
				}
				
				String serviceCategoryKey = professionName + serviceCategoryName;				
				String serviceKey = professionName + serviceCategoryName + serviceName;				
				if(!services.containsKey(serviceKey) && serviceCategories.containsKey(serviceCategoryKey)) {					
					long id = insertItemWithTags(conn, serviceName, serviceTags, "services");
					String insertServiceCommand = insertServiceCommandTemplate + "('" + id + "', '" + serviceCategories.get(serviceCategoryKey).toString() + "');";
					
					PreparedStatement statementInsertService = conn.prepareStatement(insertServiceCommand, Statement.RETURN_GENERATED_KEYS);
					statementInsertService.executeUpdate();
					ResultSet generatedKeys = statementInsertService.getGeneratedKeys();
					generatedKeys.next();
					long serviceId = generatedKeys.getLong(1);
					services.put(serviceKey, serviceId);
				}
				else
				{
					System.out.println("Duplicate key (" + serviceKey + ") or missing serviceCategory (" + serviceCategoryKey + ")");
				}
			}
			bufferedReader.close();

			System.out.println("Inserted services.");
			
			//Insert counties
			
			System.out.println("Inserting counties...");
			
			insertLocalities(conn);
			
			System.out.println("Inserted counties.");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
					//writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println("Done");
	}

	private static long insertItemWithTags(Connection conn, String itemName, String[] itemTags, String categoryType) throws SQLException {
		String insertItemCommandTemplate = "INSERT INTO professionals.items(name, fullname, categorytype) VALUES ";
		String insertItemCommand = insertItemCommandTemplate + "('" + itemName + "', '" + itemName + "', '" + categoryType + "');";
		// Get insert ID to create record in professional table 
		PreparedStatement statementInsertItem = conn.prepareStatement(insertItemCommand, Statement.RETURN_GENERATED_KEYS);			        
		statementInsertItem.executeUpdate();
		ResultSet generatedKeys = statementInsertItem.getGeneratedKeys();
		generatedKeys.next();
		long id = generatedKeys.getLong(1);
		
		// Update tags
		if (itemTags != null && itemTags.length > 0)
		{
			for (String tag : itemTags) {
				String selectTagCommand = "SELECT TagId FROM professionals.tags WHERE tagname = '" + tag + "'";
				Statement tagSelectStatement = conn.createStatement();
				ResultSet tagSelectResult = tagSelectStatement.executeQuery(selectTagCommand);
				long tagId = 0;
				// If tag doesn't already exist - insert it
				if (!tagSelectResult.next())
				{
					String insertTagCommand = "INSERT INTO professionals.tags(tagname, taglabel) VALUES ('" + tag + "', '" + tag + "')";
					PreparedStatement statementInsertTag = conn.prepareStatement(insertTagCommand, Statement.RETURN_GENERATED_KEYS);			        
					statementInsertTag.executeUpdate();
					ResultSet generatedKeysTag = statementInsertTag.getGeneratedKeys();
					generatedKeysTag.next();
					tagId = generatedKeysTag.getLong(1);
				}
				// If exists, collect tagId
				else
				{
					tagId = tagSelectResult.getLong(1);
				}

				// Connect item with tag
				String insertTagForItemCommand = "INSERT INTO professionals.tagsforitems(tagid, itemid) VALUES ('" + tagId + "', '" + id + "')";
				Statement insertTagForItemStatement = conn.createStatement();
				insertTagForItemStatement.executeUpdate(insertTagForItemCommand);

			}
		}
		
		return id;
	}
	
	private static void insertLocalities(Connection conn) {

		Map<String, Integer> counties = new HashMap<String, Integer>();
		Map<String, Integer> districts = new HashMap<String, Integer>();		
		
		String insertCountiesCommand = "INSERT INTO professionals.counties(name, path) VALUES ";
		String insertDistrictsCommand = "INSERT INTO professionals.districts(name, path, countyid) VALUES ";
		String insertLocalitiesCommand = "INSERT INTO professionals.localities(name, path, districtid, countyid) VALUES ";
		
		String csvFile = "scripts/Municipalities.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		
		try {
			br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(csvFile)), "UTF8"));
			
			int nextCountyId = 1;
			int nextDistrictId = 1;
			
			while ((line = br.readLine()) != null) {
	 
			    // use comma as separator
				String[] municipality = line.split(cvsSplitBy);
			
				String locality = municipality[0];
				String district = municipality[1];
				String county = municipality[2];
				String countyName =  county.replace(" županija", "");
				
				if(!counties.containsKey(countyName)) {					
					insertCountiesCommand += "('" + countyName + "', '" + county + "')," ;
					counties.put(countyName, nextCountyId);
					nextCountyId++;
				}
				if(!districts.containsKey(district)) {
					String districtPath = district + ", " + countyName; 
					Integer countyId = counties.get(countyName);
					insertDistrictsCommand += "('" + district + "', '" + districtPath + "'," + countyId + ")," ;
					districts.put(district, nextDistrictId);
					nextDistrictId++;
				}	
				String localityPath = locality + ", " + district + ", " + countyName;
				Integer countyId = counties.get(countyName);
				Integer districtId = districts.get(district);
				insertLocalitiesCommand += "('" + locality + "', '" + localityPath + "'," + districtId + "," + countyId + ")," ;
			}	 
			
			insertCountiesCommand = insertCountiesCommand.substring(0, insertCountiesCommand.length() - 1) + ";";
			insertDistrictsCommand = insertDistrictsCommand.substring(0, insertDistrictsCommand.length() - 1) + ";";
			insertLocalitiesCommand = insertLocalitiesCommand.substring(0, insertLocalitiesCommand.length() - 1) + ";";
			
			String command = insertCountiesCommand + "\n\n" + insertDistrictsCommand + "\n\n" + insertLocalitiesCommand;
			Statement statementDBCreate = conn.createStatement();
			statementDBCreate.executeUpdate(command);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println("Inserted localities");
	}
}
