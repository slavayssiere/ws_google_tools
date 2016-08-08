package org.crf.ws.services;

import org.crf.google.GConnectToken;
import org.crf.models.Session;

import com.google.api.services.calendar.model.Event;

public interface CalendarService {

	Event create(Session sess) throws Exception;
	void setToken(GConnectToken gct);

}