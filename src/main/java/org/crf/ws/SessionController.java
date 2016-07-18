package org.crf.ws;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.crf.models.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
			nextId=1;
		}
		if(session.getId() != null){
			Session oldsession = sessionsMap.get(session.getId());
			if(oldsession==null){
				return null;
			}
			sessionsMap.remove(session.getId());
			sessionsMap.put(session.getId(), session);
			return session;
		}
		session.setId(nextId);
		nextId = nextId + 1;
		sessionsMap.put(nextId, session);
		return session;
	}

	public static boolean delete(Session session){
		Session delsession = sessionsMap.remove(session.getId());
		if(delsession == null){
			return false;
		}
		else {
			return true;
		}
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
	
	@RequestMapping(
			value="/api/sessions/{id}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Session> getSession(@PathVariable("id") Integer Id){
		
		Session session = sessionsMap.get(Id);
		if(session == null){
			return new ResponseEntity<Session>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Session>(session, HttpStatus.OK);
	}
	
	@RequestMapping(
			value="/api/sessions", 
			method=RequestMethod.POST, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Session> createSession(@RequestBody Session session) {
		
		Session savesession = save(session);
		
		return new ResponseEntity<Session>(savesession, HttpStatus.CREATED);		
	}
	
	@RequestMapping(
			value="/api/sessions", 
			method=RequestMethod.PUT, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Session> updateSession(@RequestBody Session session) {
		
		Session savesession = save(session);
		
		if(savesession == null){
			return new ResponseEntity<Session>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Session>(savesession, HttpStatus.OK);		
	}
	
	@RequestMapping(
			value="/api/sessions/{id}",
			method=RequestMethod.DELETE,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Session> deleteSession(@PathVariable("id") Integer Id){
		
		boolean testdelete = delete(sessionsMap.get(Id));
		
		if(!testdelete){
			return new ResponseEntity<Session>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Session>(HttpStatus.NO_CONTENT);
	}
	
	
}
