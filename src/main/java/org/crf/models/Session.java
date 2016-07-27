package org.crf.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Session {

	private String formateur;
	
	private String address;
	
	private Date date;
	
	private Integer id;
	
	private String type;
	
	private String google_id;
	
	private String google_name;
	
	private List<Inscription> inscriptions;
	
	private List<Integer> emptyRows;
	
	public Session(){
		setInscriptions(new ArrayList<Inscription>());
		setEmptyRows(new ArrayList<Integer>());
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGoogle_id() {
		return google_id;
	}

	public void setGoogle_id(String google_id) {
		this.google_id = google_id;
	}

	public String getGoogle_name() {
		return google_name;
	}

	public void setGoogle_name(String google_name) {
		this.google_name = google_name;
	}

	public List<Inscription> getInscriptions() {
		return inscriptions;
	}

	private void setInscriptions(List<Inscription> inscriptions) {
		this.inscriptions = inscriptions;
	}
	
	public void addInscription(Inscription insc){
		this.inscriptions.add(insc);
	}

	public List<Integer> getEmptyRows() {
		return emptyRows;
	}

	private void setEmptyRows(List<Integer> emptyRows) {
		this.emptyRows = emptyRows;
	}
	
	public void addEmptyRow(Integer row){
		this.emptyRows.add(row);
	}
	
	@JsonProperty("nb_empty")
	public Integer getNbEmpty(){
		return this.emptyRows.size();
	}
}
