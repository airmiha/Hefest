package com.hefest.app.model;

import java.sql.Date;

public class Testimonial {

	private int professionalId;
	private String imagePath;
	private String summary;
	private String text;
	private String personName;
	private String personCompany;
	private Date date;
	private String ownersReply;
	private Date replyDate;
	private int total;

	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getProfessionalId() {
		return professionalId;
	}
	public void setProfessionalId(int professionalId) {
		this.professionalId = professionalId;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getPersonCompany() {
		return personCompany;
	}
	public void setPersonCompany(String personCompany) {
		this.personCompany = personCompany;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getOwnersReply() {
		return ownersReply;
	}
	public void setOwnersReply(String ownersReply) {
		this.ownersReply = ownersReply;
	}
	public Date getReplyDate() {
		return replyDate;
	}
	public void setReplyDate(Date replyDate) {
		this.replyDate = replyDate;
	}
}
