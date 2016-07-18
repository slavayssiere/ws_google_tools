package org.crf.ws.services;

import java.util.Collection;

import org.crf.models.Session;

public interface SessionService {

	Collection<Session> findAll();
	
	Session findOne(Integer id);
	
	Session create(Session session);
	
	Session update(Session session);
	
	void delete(Integer id);
}
