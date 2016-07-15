package com.hefest.app.loaders;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ProfessionalsInitializer {

	private static String[] addresses = {"Kralja Zvonimira 6","Zagrebačka 10","Srednjaci 1","Turopoljska 45","Dolenec 11","Kralja Stjepana Tomaševića 90","Kralja Petra Krešimira 25","Sisačka 120","Stara Ulica 2","Stjepana Radića 47","Kvaternikov trg 32","Seljine brigade 87","Trešnjevačka cesta 3","Ilica 135","Pustike 90","Nova cesta 2","Markuševec 82"};
	private static String[] firstNames = {"Miro","Pero","Marko","Zdravko","Hrvoje","Ivan","Stjepan","Marin","Igor","Mario","Josip","Vinko","Luigi","Tomislav","Ana","Tanja","Marin"};
	private static String[] lastNames = {"Mirković","Perić","Marković","Kovačević","Babić","Marić","Novak","Jurić","Kovačić","Vukas","Knežević","Vuković","Petrović","Popović","Robić", "Andrić", "Markulin"};
	private static String[] yearsOfEstablishment = {"1990","1991","1992","1993","1995","1997","1999","2001","2003","2001","2003","2005","2007","2009","2011", "2013"};


	public static void main(String[] args) throws SQLException {	 
					
		try {
	
			// Connect to database 
			String url = "jdbc:postgresql://localhost/geoads";
			String username = "geoads";
			String password = "geoads";
			Connection conn = DriverManager.getConnection(url, username, password);
	
			String command = "TRUNCATE professionals.addresses, professionals.itemsforprofessionals, professionals.professionalsforcounties RESTART IDENTITY CASCADE;";			
			command += getInsertProfessionalsSQL();
	
			Statement statementDBCreate = conn.createStatement();
			statementDBCreate.executeUpdate(command);
			
			System.out.println("Adding an admin user.");
			
			String insertAdminCommand = "INSERT INTO professionals.addresses(addressline) VALUES (''); ";
			
			PreparedStatement statementInsertAdmin = conn.prepareStatement(insertAdminCommand, Statement.RETURN_GENERATED_KEYS);
			statementInsertAdmin.executeUpdate();
			ResultSet generatedKeys = statementInsertAdmin.getGeneratedKeys();
			generatedKeys.next();
			long addressId = generatedKeys.getLong(1);
			PasswordEncoder encoder = new BCryptPasswordEncoder();
			String adminPassword = encoder.encode("admin");
			insertAdminCommand = "INSERT INTO professionals.users(name, password, email, role, addressid) VALUES ('Admin','"  
			+ adminPassword + "', 'admin@hefestmail.com', 'ROLE_ADMIN'," + addressId + ");";
			statementInsertAdmin = conn.prepareStatement(insertAdminCommand, Statement.RETURN_GENERATED_KEYS);
			statementInsertAdmin.executeUpdate();
			System.out.println("Added admin user.");
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		 
		System.out.println("Done");
	}

	private static String getInsertProfessionalsSQL() {
		String insertAddress = "INSERT INTO professionals.addresses(addressline, lat, long, LatLong) VALUES";
		String insertUser = "INSERT INTO professionals.users(addressid, name, password, telephone, mobile, role, leadimage, backgroundimage) VALUES";
		String insertProfessional = "INSERT INTO professionals.professionals(userid, pagetitle, pageurl, metadescription, ownername, professions, profileheadline, "
				+ "profilesubheadline, profilemoneyshot, featuresheadline, featuressubheadline, employeecount, score, yearestablished, "
				+ "contactemail, fax, website, projectcount, endorsementcount, description, reviewcount, avgreview, verified) VALUES";		
		String insertCountiesForProfessionals = "INSERT INTO professionals.professionalsforcounties(professionalid, countyid) VALUES "; 	
		String insertProfessionItems = "INSERT INTO professionals.itemsforprofessionals(professionalid, itemid, professionaldescription) VALUES";
		String insertImages = "INSERT INTO professionals.imagesforprofessionals(professionalid, path, description) VALUES";
		String insertTestimonials = "INSERT INTO professionals.testimonials(professionalid, imagepath, text, summary, personname, personcompany, projectid, date, ownersreply, replydate, isselected) VALUES";
		String insertFeatures = "INSERT INTO professionals.features(professionalid, icon, headline, description) VALUES";
		String insertProjects = "INSERT INTO professionals.projects(professionalid, addressid, name, leadimage, imagecount, description, cost, currency, datestarted, dateperformed, "
	            + "projectduration, likescount, commentcount, metadescription, pageurl, pagetitle) VALUES";
		String insertEndorsements = "INSERT INTO professionals.endorsements(professionalid, userid, professionid) VALUES";
		List<String> insertCategoryItemsForProfessionals = new ArrayList<String>();
		int profCount = 50;
		
		for (int i=1; i < profCount; i++) {
			String addressLine = (addresses[new Random().nextInt(addresses.length)]);
			Double latitude = 45.303845 + (45.998844 - 45.303845) * new Random().nextDouble();
			Double longitude = 15.779714 + (16.977224 - 15.779714) * new Random().nextDouble();	
			String pointObj = String.format(Locale.ENGLISH, "ST_GeomFromText('POINT(%f %f)')", latitude, longitude);
			insertAddress += " ('" + addressLine + "'," + latitude + "," + longitude + "," + pointObj + "),";
			String firstName =  firstNames[new Random().nextInt(firstNames.length)];
			String lastName = lastNames[new Random().nextInt(lastNames.length)];
			String ownerName = firstName + " " + lastName;
			String name = ownerName + " d.o.o";
			String backgroundImage = "profile-bg.jpg"; 
			String leadImage = "img/mock/" + new Random().nextInt(14) + ".jpg"; 
			String password = "";
			String telephone ="";
			String role = "ROLE_PROFESSIONAL";
			String mobile = "";			
			insertUser += " (" + i + ",'" + name + "','" + password + "','" + telephone + "','" + mobile + "','" + role + "','" + leadImage + "','" + backgroundImage + "'),";
			String pageTitle = name;
			String pageURL = firstName + "-" + lastName + "-" + "d.o.o";
			String metaDescription = name;
			String profileMoneyShot = new Random().nextInt(14) + ".jpg";
			String profileHeadline = "Get your work done";
			String profileSubHeadline = "We offer competitive prices and a 5 year warranty on all home improvement projects";
			String featuresHeadline = "Get an offer you cannot refuse";
			String featuresSubHeadline = "See how we are different from the competition";
			Integer employeecount = new Random().nextInt(20);
			Integer score = new Random().nextInt(300);
			String yearEstablished = (yearsOfEstablishment[new Random().nextInt(yearsOfEstablishment.length)]);
			String contactEmail = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com";
			String fax = "";
			String website = firstName.toLowerCase() + lastName.toLowerCase() + ".hr";
			Integer projectcount = new Random().nextInt(100);
			Integer endorsementcount = new Random().nextInt(100);
			String description = getDescription();
			Integer reviewcount = new Random().nextInt(100);
			Double avgReview = new Random().nextDouble() * 5;
			boolean verified = new Random().nextBoolean();
		
			Integer professionId = new Random().nextInt(24) + 6;
					
			insertProfessionItems+= " (" + i + ", (SELECT itemid FROM professionals.professions WHERE professions.professionid=" + professionId + "),''),";
			
			String professions = "(SELECT name FROM professionals.items"
						+ " JOIN professionals.professions ON items.itemid=professions.itemid" 
						+ " WHERE professions.professionid=" + professionId + ")";
			
			insertProfessional += " (" + i + ",'" + pageTitle + "','" + pageURL + "','" + metaDescription + "','" + ownerName + "'," + professions + ",'" + profileHeadline + "','" + profileSubHeadline 
					+ "','" + profileMoneyShot + "','" + featuresHeadline + "','" + featuresSubHeadline + "'," + employeecount + "," 
					+ score + ",'" + yearEstablished + "','" + contactEmail + "','" + fax + "','" + website + "'," + projectcount + "," + endorsementcount + ",'"
					+ description + "'," + reviewcount + "," + avgReview + "," + verified +  "),";
			
			for (int j=0; j < 25; j++) {
				insertImages += " (" + i + ",'" + j + ".jpg'," + "'Awesome image " + j + "'),";
			}
			
			
			String professionalDescription = "Opis usluge";
			
			int numberOfCounties = 2 + new Random().nextInt(20);
			
			for(int j=1; j < numberOfCounties; j++) {
				insertCountiesForProfessionals += " (" + i + "," + j + "),";
			}
			
			// Features
			for(int j=0; j < 3; j++) {
				String icon;
				String headline;
				String descriptionn;
				if (j == 0) {
					icon = "fa fa-key";
					headline = "Complete service";
					descriptionn = "Your home will be tidy and clean after the job";
				} else if (j == 1) {
					icon = "fa fa-smile-o";
					headline = "100% satisfaction";
					descriptionn = "We never had a customer that was not satisfied";
				} else {
					icon = "fa fa-certificate";
					headline = "10 years guarantee";
					descriptionn = "You get a 10 year guarantee on all of our services";
				}
				
				insertFeatures += " (" + i + ",'" + icon + "','" + headline + "','" + descriptionn + "'),";
			}
			
			// Projects
			for(int j=0; j < 30; j++) {
				String projectName = "Random project " + j;
				String projectLeadimage = "img/mockprojects/" + new Random().nextInt(22) + ".jpg";
				int projectImageCount =  new Random().nextInt(22);
				String projectDescription = "This was a project for a family that were our old customers. We did work around their house and had to hurry because winter was coming. The customers, as well as ourselves, were very satisfied."; 
				double cost = new Random().nextInt(100000); 
				String currency = "€"; 
				Date datestarted = new Date((new java.util.Date()).getTime()); 
				Date dateperformed = new Date((new java.util.Date()).getTime());  
				int projectduration = 10; 
				int likescount = 200; 
				int	commentcount = 350; 
				String metadescription = "Meta description"; 
				String pageurl = "Random project " + j; 
				String pagetitle = "Random project " + j;
				
				insertProjects += " (" + i + "," + i + ",'" + projectName + "','" + projectLeadimage + "'," + projectImageCount + ",'" + projectDescription + "'," + cost + ",'" + currency + "','" + datestarted + "','" + dateperformed + "'," + projectduration + "," + likescount + "," + commentcount + ",'" + metadescription + "','" + pageurl + "','" + pagetitle + "'),";
			
				// Testimonial for project
				String customerImage = "customer" + (j%3 + 1 + "") + ".png";
				String customerName = firstNames[new Random().nextInt(firstNames.length)];
				String customerCompany = "Company " + (j%3 + 1 + "");
				Date date = new Date((new java.util.Date()).getTime());
				String ownerReply = null;
				boolean isSelected = false;
				if(j<3) {
					isSelected = true;
				}
				if(j<3) {
					ownerReply = "'Thank you very much'";
				}
				insertTestimonials += " (" + i + ",'" + customerImage + "','" + getRandomReviewText(j%3) + "','Exceptional service.','" + customerName + "','" + customerCompany + "'," + (j+1) + ",'" + date + "'," + ownerReply + ",'" + date + "'," + isSelected + "),";
			}
			
			// Endorsements
			for(int j=0; j < 30; j++) {
				insertEndorsements += " (" + i + "," + (j+1) + "," + professionId + "),";
			}
					
			String insertServiceCategoryItemsForProfessional = "INSERT INTO professionals.itemsforprofessionals(professionalid, itemid, professionaldescription) "
					+ "SELECT " + i + ", itemid, '" + professionalDescription + "' FROM professionals.servicecategories WHERE professionid=" + professionId + ";";
			
			insertCategoryItemsForProfessionals.add(insertServiceCategoryItemsForProfessional);
			if ((profCount > 100) && (i % 100 == 0)){
				System.out.println(String.format("Initialized %d / %d", i, profCount));			
			} 			
		}
		
		insertAddress = insertAddress.substring(0, insertAddress.length() - 1) + ";";
		insertUser = insertUser.substring(0, insertUser.length() - 1) + ";";
		insertProfessional = insertProfessional.substring(0, insertProfessional.length() - 1) + ";";
		insertImages = insertImages.substring(0, insertImages.length() - 1) + ";";
		insertFeatures = insertFeatures.substring(0, insertFeatures.length() - 1) + ";";
		insertProfessionItems = insertProfessionItems.substring(0, insertProfessionItems.length() - 1) + ";";
		insertCountiesForProfessionals = insertCountiesForProfessionals.substring(0, insertCountiesForProfessionals.length() - 1) + ";";
		insertProjects = insertProjects.substring(0, insertProjects.length() - 1) + ";";
		insertTestimonials = insertTestimonials.substring(0, insertTestimonials.length() - 1) + ";";
		insertEndorsements = insertEndorsements.substring(0, insertEndorsements.length() - 1) + ";";
		
		String result = insertAddress + insertUser + insertProfessional + insertImages + insertFeatures + insertProfessionItems + insertCountiesForProfessionals + insertProjects + insertTestimonials + insertEndorsements;
		for(String insertCategoryItemsForProfessional : insertCategoryItemsForProfessionals) {
			result += insertCategoryItemsForProfessional;
		}
//		System.out.println(result);
		return result;
	
	}

	private static String getDescription() {
		String description = "Our company has 30 years of experience in construction. We have done quality work throughout the country and have maximum customer satisfaction."
				+ "We have qualified teams of professionals for all types of construction projects and guarantee availability at any time. We offer the highest warranty averages"
				+ "in the nation. That is how much we believe in the value we give. Our work is not a commodity but a genuine service with high standards. Our work has also been"
				+ "recognized abroad where we had multiple large scale projects in big metro areas.";
		return description;
	}

	private static String getRandomReviewText(int i) {
		if (i == 0) {
			return "We are very satisfied with the service and end result. The company was professional, knowledgeable and helpful.";
		} else if (i == 1) {
			return "We will hire the same company for all of our future work and we recommend them to all of our friends. They are that good in what they do.";
		}
		return "The work they did in our house was outstanding and at a surprisingly good price. We get lots of compliments from our neighbours and friends ";
	}
}
