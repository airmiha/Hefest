package com.hefest.app.model;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class Professional extends User {

	private int professionalId;
	private int userId;
	private String ownerName;
	private String logo;
	private String professions;
	private String pageTitle;
	private String pageUrl;
	private String metaDescription;
	private String profileHeadline;
	private String profileSubHeadline;
	private String profileMoneyShot;
	private String featuresHeadline;
	
	private String featuresSubHeadline;
	private Integer employeeCount;
	private Integer score;
	private Integer yearEstablished;
	private String contactEmail;
	private String fax;
	private String website;
	private Integer projectCount;

	private Integer endorsementCount;
	private String description;
	private Integer reviewCount;
	private Double avgReview;
	private boolean verified;
	private Date signupDate;
	
	private List<String> items;
	private List<Integer> counties;
	private Map<Integer, Map<String, Object>> professionsAndServices; 
	private List<Image> images;
	private List<Testimonial> testimonials;
	private List<Feature> features;
		
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public String getProfileMoneyShot() {
		return profileMoneyShot;
	}
	public void setProfileMoneyShot(String profileMoneyShot) {
		this.profileMoneyShot = profileMoneyShot;
	}
	
	public String getFeaturesHeadline() {
		return featuresHeadline;
	}
	public void setFeaturesHeadline(String featureHeadline) {
		this.featuresHeadline = featureHeadline;
	}
	public String getFeaturesSubHeadline() {
		return featuresSubHeadline;
	}
	public void setFeaturesSubHeadline(String featureSubHeadline) {
		this.featuresSubHeadline = featureSubHeadline;
	}
	
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	
	public String getMetaDescription() {
		return metaDescription;
	}
	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}
	public void setProfessionalId(int professionalId) {
		this.professionalId = professionalId;
	}
	
	public List<String> getItems() {
		return items;
	}
	public void setItems(List<String> items) {
		this.items = items;
	}
	public List<Integer> getCounties() {
		return counties;
	}
	public void setCounties(List<Integer> counties) {
		this.counties = counties;
	}
	public int getProfessionalId() {
		return professionalId;
	}

	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getProfessions() {
		return professions;
	}
	public void setProfessions(String professions) {
		this.professions = professions;
	}
	public String getProfileHeadline() {
		return profileHeadline;
	}
	public void setProfileHeadline(String profileHeadline) {
		this.profileHeadline = profileHeadline;
	}
	public String getProfileSubHeadline() {
		return profileSubHeadline;
	}
	public void setProfileSubHeadline(String profileSubHeadline) {
		this.profileSubHeadline = profileSubHeadline;
	}
	
	public Integer getEmployeeCount() {
		return employeeCount;
	}
	public void setEmployeeCount(Integer employeeCount) {
		this.employeeCount = employeeCount;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public Integer getYearEstablished() {
		return yearEstablished;
	}
	public void setYearEstablished(Integer yearEstablished) {
		this.yearEstablished = yearEstablished;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public Integer getProjectCount() {
		return projectCount;
	}
	public void setProjectCount(Integer projectCount) {
		this.projectCount = projectCount;
	}
	public Integer getEndorsementCount() {
		return endorsementCount;
	}
	public void setEndorsementCount(Integer endorsementCount) {
		this.endorsementCount = endorsementCount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
	}
	public Double getAvgReview() {
		return avgReview;
	}
	public void setAvgReview(Double avgReview) {
		this.avgReview = avgReview;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public Date getSignupDate() {
		return signupDate;
	}
	public void setSignupDate(Date signupDate) {
		this.signupDate = signupDate;
	}
	public Map<Integer, Map<String, Object>> getProfessionsAndServices() {
		return professionsAndServices;
	}
	public void setProfessionsAndServices(Map<Integer, Map<String, Object>> professionsAndServices) {
		this.professionsAndServices = professionsAndServices;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
	public List<Image> getImages() {
		return images;
	}
	public List<Testimonial> getTestimonials() {
		return testimonials;
	}
	public void setTestimonials(List<Testimonial> testimonials) {
		this.testimonials = testimonials;
	}
	public List<Feature> getFeatures() {
		return features;
	}
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
}
