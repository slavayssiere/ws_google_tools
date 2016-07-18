package org.crf.ws;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.crf.models.Session;
import org.crf.ws.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private SessionService sessionService;
	
	@RequestMapping(
			value="/api/sessions", 
			method=RequestMethod.GET, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Session>> getSessions() {
		
		Collection<Session> listsession = sessionService.findAll();
		
		return new ResponseEntity<Collection<Session>>(listsession, HttpStatus.OK);		
	}
	
	@RequestMapping(
			value="/api/sessions/{id}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Session> getSession(@PathVariable("id") Integer id){
		
		Session session = sessionService.findOne(id);
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
		
		Session savesession = sessionService.create(session);
		
		return new ResponseEntity<Session>(savesession, HttpStatus.CREATED);		
	}
	
	@RequestMapping(
			value="/api/sessions", 
			method=RequestMethod.PUT, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Session> updateSession(@RequestBody Session session) {
		
		Session savesession = sessionService.update(session);
		
		if(savesession == null){
			return new ResponseEntity<Session>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Session>(savesession, HttpStatus.OK);		
	}
	
	@RequestMapping(
			value="/api/sessions/{id}",
			method=RequestMethod.DELETE,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Session> deleteSession(@PathVariable("id") Integer id){
		
		sessionService.delete(id);
		
		
		return new ResponseEntity<Session>(HttpStatus.NO_CONTENT);
	}
	
	
}
