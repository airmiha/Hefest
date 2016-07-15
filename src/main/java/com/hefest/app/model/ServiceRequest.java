package com.hefest.app.model;

import java.sql.Date;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ServiceRequest {
	
	private int serviceRequestId;
	private int localityId;
	private String localityName;
	private int userId;	
	private java.util.Date requestDateAndTime;
	private Date requestDate;
	
	@Size(min = 1, max = 100)
	private String nameOfContact;
	@Size(min = 1, max = 1000)
	private String contactNumber;
	
	@Size(min = 1, max = 255)
	@Email
	private String contactEmail;
	private String bestTimeToContact;
	private Integer offerCount;
	
	@Size(min = 1, max = 1000000000)
	private Double maxPrice;
	private boolean willProvideMaterials;
	private boolean verified;
	
	private Date endBy;
	private Date startBy;
	
	private List<Integer> itemIds;
	private List<Image> images;
	
	public List<Integer> getItemIds() {
		return itemIds;
	}

	public void setItemIds(List<Integer> itemIds) {
		this.itemIds = itemIds;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public int getServiceRequestId() {
		return serviceRequestId;
	}
	
	public void setServiceRequestId(int serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}
	public int getLocalityId() {
		return localityId;
	}
	public void setLocalityId(int localityId) {
		this.localityId = localityId;
	}
	public String getLocalityName() {
		return localityName;
	}
	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public java.util.Date getRequestDateAndTime() {
		return requestDateAndTime;
	}
	public void setRequestDateAndTime(java.util.Date requestDateAndTime) {
		this.requestDateAndTime = requestDateAndTime;
		this.requestDate = new java.sql.Date(requestDateAndTime.getTime());
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
		this.requestDateAndTime = requestDate;
	}
	public String getNameOfContact() {
		return nameOfContact;
	}
	public void setNameOfContact(String nameOfContact) {
		this.nameOfContact = nameOfContact;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getBestTimeToContact() {
		return bestTimeToContact;
	}
	public void setBestTimeToContact(String bestTimeToContact) {
		this.bestTimeToContact = bestTimeToContact;
	}
	public Integer getOfferCount() {
		return offerCount;
	}
	public void setOfferCount(Integer offerCount) {
		this.offerCount = offerCount;
	}
	public Double getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}
	public boolean isWillProvideMaterials() {
		return willProvideMaterials;
	}
	public void setWillProvideMaterials(boolean willProvideMaterials) {
		this.willProvideMaterials = willProvideMaterials;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public Date getStartBy() {
		return startBy;
	}
	public void setStartBy(Date startBy) {
		this.startBy = startBy;
	}
	
	public void setStartByDate(java.util.Date startByDate) {
		this.startBy = new Date(startByDate.getTime());
	}
	
	public Date getEndBy() {
		return endBy;
	}
	
	public void setEndBy(Date endBy) {
		this.endBy = endBy;
	}
	
	public void setEndByDate(java.util.Date endByDate) {
		this.endBy = new Date(endByDate.getTime());
	}
}