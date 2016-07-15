package com.hefest.app.model;

import java.sql.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class User {

	private int userId;
	
	@Size(min = 1, max = 100)
	private String name;
	
	@Size(min = 6, max = 100)
	private String password;
	
	@Size(min = 0, max = 255)
	@Email
	private String email;
	
	private Integer addressId;
	private boolean active;
	private String activationKey;
	private String resetPasswordToken;
    private LocalDate resetPasswordTokenLocalDate;
    private Date resetPasswordTokenDate;
    private String addressLine;
    private Double latitude;
	private Double longitude;
	private String leadImage;
	private String backgroundImage;
	
	public String getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	private String mobile;
	private String telephone;
	private String role;
    
	public String getAddressLine() {
		return addressLine;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}

	public String getLeadImage() {
		return leadImage;
	}

	public void setLeadImage(String leadImage) {
		this.leadImage = leadImage;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public User() {
		this.role = "ROLE_USER";
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public LocalDate getResetPasswordTokenLocalDate() {
		return resetPasswordTokenLocalDate;
	}

	public void setResetPasswordTokenLocalDate(
			LocalDate resetPasswordTokenLocalDate) {
		this.resetPasswordTokenLocalDate = resetPasswordTokenLocalDate;
		this.resetPasswordTokenDate = new java.sql.Date(resetPasswordTokenLocalDate.toDate().getTime());
	}

	public Date getResetPasswordTokenDate() {
		return resetPasswordTokenDate;
	}

	public void setResetPasswordTokenDate(Date resetPasswordTokenDate) {
		this.resetPasswordTokenDate = resetPasswordTokenDate;
		this.resetPasswordTokenLocalDate = new LocalDate(resetPasswordTokenDate);
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
	public String getAddressline() {
		return addressLine;
	}
	public void setAddressline(String addressLine) {
		this.addressLine = addressLine;
	}
}
