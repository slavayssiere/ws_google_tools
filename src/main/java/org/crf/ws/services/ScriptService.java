package org.crf.ws.services;

import org.crf.google.GoogleConnection;
import org.crf.models.ScriptData;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

public interface ScriptService {

	void setToken(GoogleConnection newgct);

	ScriptData execute(ScriptData sr) throws GoogleJsonResponseException;

}