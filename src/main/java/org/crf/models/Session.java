package org.crf.models;

import java.util.Date;

public class Session {

	private String formateur;
	
	private String address;
	
	private Date date;
	
	private Integer id;
	
	public Session(){
		
	}

	public String getFormateur() {
		return formateur;
	}

	public void setFormateur(String formateur) {
		this.formateur = formateur;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
