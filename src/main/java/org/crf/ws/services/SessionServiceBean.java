package org.crf.ws.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.crf.models.Session;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceBean implements SessionService {


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

	public static boolean remove(Integer id){
		Session delsession = sessionsMap.remove(id);
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
	
	
	@Override
	public Collection<Session> findAll() {
		Collection<Session> listsession = sessionsMap.values();
		return listsession;
	}

	@Override
	public Session findOne(Integer id) {
		Session session = sessionsMap.get(id);
		return session;
	}

	@Override
	public Session create(Session session) {
		Session savesession = save(session);
		return savesession;
	}

	@Override
	public Session update(Session session) {
		Session savesession = save(session);
		return savesession;
	}

	@Override
	public void delete(Integer id) {
		remove(id);

	}

}
