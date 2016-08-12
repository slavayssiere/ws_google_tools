package org.crf.ws.services;

import java.util.Collection;

import org.crf.google.GoogleConnection;
import org.crf.models.FileDrive;
import org.crf.models.Session;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

public interface DriveService {

	Collection<FileDrive> findAll() throws Exception;

	Session copy(String fileId, Session sess) throws GoogleJsonResponseException;

	FileDrive findOne(String titleFile) throws Exception;
	
	void setToken(GoogleConnection gct);

}