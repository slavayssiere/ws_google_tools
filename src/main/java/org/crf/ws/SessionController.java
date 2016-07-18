package org.crf.ws;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.crf.models.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
	
	public static Integer nextId;
	public static Map<Integer, Session> sessionsMap;
	
	public static Session save(Session session){
		if(sessionsMap == null){
			sessionsMap = new HashMap<Integer, Session>();
			nextId=0;
		}
		session.setId(nextId);
		nextId = nextId + 1;
		sessionsMap.put(nextId, session);
		return session;
	}
	
	static {
		Session session1 = new Session();
		session1.setFormateur("toto");
		save(session1);
		
		Session session2 = new Session();
		session2.setFormateur("titi");
		save(session2);			
	}
	
	@RequestMapping(
			value="/api/sessions", 
			method=RequestMethod.GET, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Session>> getSessions() {
		
		Collection<Session> listsession = sessionsMap.values();
		
		return new ResponseEntity<Collection<Session>>(listsession, HttpStatus.OK);		
	}
}
