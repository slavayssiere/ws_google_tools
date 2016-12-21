package org.crf.ws.services;

import java.util.Collection;
import java.util.Date;

import org.crf.google.GoogleConnection;
import org.crf.models.FileDrive;
import org.crf.models.Session;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

public interface DriveService {

	Collection<FileDrive> findAll(String year) throws Exception;
	
	Collection<FileDrive> findAllAfter(Date dates) throws Exception;

	Session copy(String fileId, Session sess) throws GoogleJsonResponseException;

	FileDrive findOne(String titleFile) throws Exception;
	
	void setToken(GoogleConnection gct);

}