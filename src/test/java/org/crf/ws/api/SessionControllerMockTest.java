package org.crf.ws.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.crf.AbstractControllerTest;
import org.crf.google.GoogleConnection;
import org.crf.models.Session;
import org.crf.ws.InscriptionController;
import org.crf.ws.SessionController;
import org.crf.ws.services.CalendarService;
import org.crf.ws.services.DriveService;
import org.crf.ws.services.SessionService;
import org.crf.ws.services.SheetService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

//@Transactional to remove test data from BDD
public class SessionControllerMockTest extends AbstractControllerTest {
	
	@Mock
	private GoogleConnection gconnect;
	
	@Mock
	private CalendarService calendarService;
	
	@Mock
	private DriveService driveService;
	
	@Mock
	private SheetService sheetService;
	
	@Mock 
	private SessionService sessionService;
	
	@InjectMocks
	private SessionController controller;
	
	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		
		super.setUp(controller);
	}
	
	@Test
	public void testGetState() throws Exception{
		
		Collection<Session> list = getListSessions();
		
		when(sessionService.findAll()).thenReturn(list);
		
		String uri = "/api/sessions/";
		
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON)).andReturn();
		
		String content = result.getResponse().getContentAsString();		
		int status = result.getResponse().getStatus();
		
		verify(sessionService, times(1)).findAll();
		
		assertThat(status).isEqualTo(200);
		assertThat(content.trim().length() > 0).isTrue();
	}

	private Collection<Session> getListSessions() {
		
		List<Session> sessionsRet = new ArrayList<Session>();
		
		Session session1 = new Session();
		session1.setFormateur("toto");
		sessionsRet.add(session1);
		
		Session session2 = new Session();
		session2.setFormateur("titi");
		sessionsRet.add(session2);	
		
		
		return sessionsRet;
	}
}
