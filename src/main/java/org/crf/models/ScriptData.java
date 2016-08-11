package org.crf.models;

import java.util.ArrayList;
import java.util.List;

public class ScriptData {
	
	private List<String> data;
	private String scriptId;
	private String sheetId;
	private String functionName;
	private int nbLines;
	private String error = null;
	
	private List<String> listArguments;
	
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	public String getScriptId() {
		return scriptId;
	}
	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}
	public String getSheetId() {
		return sheetId;
	}
	public void setSheetId(String sheetId) {
		this.sheetId = sheetId;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public int getNbLines() {
		return nbLines;
	}
	public void setNbLines(int nbLines) {
		this.nbLines = nbLines;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public List<String> getListArguments() {
		if(this.listArguments == null){
			this.listArguments = new ArrayList<String>();
		}
		
		return listArguments;
	}
	public void setListArguments(List<String> listArguments) {
		this.listArguments = listArguments;
	}
	public void setNewArguments(String sheetid2) {
		if(this.listArguments == null){
			this.listArguments = new ArrayList<String>();
		}
		
		this.listArguments.add(sheetid2);		
	}

}
