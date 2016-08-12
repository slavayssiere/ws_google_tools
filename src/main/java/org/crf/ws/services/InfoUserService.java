package org.crf.ws.services;

import org.crf.google.GoogleConnection;

public interface InfoUserService {

	void setToken(GoogleConnection newgct);

	String getUserEmail();

}