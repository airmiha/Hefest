package com.hefest.app.model;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class Project {

	private String companyName;
	
    private String addressLine;
    private String locality;
    private Double latitude;
	private Double longitude;
	private String leadImage;
	private Integer imageCount;
	
	private String name;
	private String description;
	private String projectCost;
	private String currency;

	private Date dateStarted;
	private Date datePerformed;
	private Integer projectDuration;
	
	private Integer likesCount;
	private Integer commentCount;
	private String pageTitle;
	private String pageUrl;
	private String metaDescription;
	
	private List<String> items;
	private List<Integer> counties;
	private Map<Integer, Map<String, Object>> professionsAndServices; 
	private List<Image> images;
	private Testimonial testimonial;
	private List<Comment> comments;
	
	
	public void setImages(List<Image> images) {
		// TODO Auto-generated method stub
		
	}


	public String getCompanyName() {
		return companyName;
	}


	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public String getAddressLine() {
		return addressLine;
	}


	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}


	public String getLocality() {
		return locality;
	}


	public void setLocality(String locality) {
		this.locality = locality;
	}


	public Double getLatitude() {
		return latitude;
	}


	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}


	public Double getLongitude() {
		return longitude;
	}


	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}


	public String getLeadImage() {
		return leadImage;
	}


	public void setLeadImage(String leadImage) {
		this.leadImage = leadImage;
	}


	public Integer getImageCount() {
		return imageCount;
	}


	public void setImageCount(Integer imageCount) {
		this.imageCount = imageCount;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getProjectCost() {
		return projectCost;
	}


	public void setProjectCost(String projectCost) {
		this.projectCost = projectCost;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public Date getDateStarted() {
		return dateStarted;
	}


	public void setDateStarted(Date dateStarted) {
		this.dateStarted = dateStarted;
	}


	public Date getDatePerformed() {
		return datePerformed;
	}


	public void setDatePerformed(Date datePerformed) {
		this.datePerformed = datePerformed;
	}


	public Integer getProjectDuration() {
		return projectDuration;
	}


	public void setProjectDuration(Integer projectDuration) {
		this.projectDuration = projectDuration;
	}


	public Integer getLikesCount() {
		return likesCount;
	}


	public void setLikesCount(Integer likesCount) {
		this.likesCount = likesCount;
	}


	public Integer getCommentCount() {
		return commentCount;
	}


	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}


	public String getPageTitle() {
		return pageTitle;
	}


	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}


	public String getPageUrl() {
		return pageUrl;
	}


	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}


	public String getMetaDescription() {
		return metaDescription;
	}


	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
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


	public Map<Integer, Map<String, Object>> getProfessionsAndServices() {
		return professionsAndServices;
	}


	public void setProfessionsAndServices(
			Map<Integer, Map<String, Object>> professionsAndServices) {
		this.professionsAndServices = professionsAndServices;
	}


	public Testimonial getTestimonial() {
		return testimonial;
	}


	public void setTestimonial(Testimonial testimonial) {
		this.testimonial = testimonial;
	}


	public List<Image> getImages() {
		return images;
	}


	public List<Comment> getComments() {
		return comments;
	}


	public void setComments(List<Comment> comments2) {
		// TODO Auto-generated method stub
		
	}
}
