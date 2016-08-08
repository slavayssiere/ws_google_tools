package org.crf.ws.services;

import org.crf.google.GConnectToken;

public interface InfoUserService {

	void setToken(GConnectToken newgct);

	String getUserEmail();

}