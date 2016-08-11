package org.crf.ws.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.crf.AbstractControllerTest;
import org.crf.ws.services.SessionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


public class SessionControllerTest extends AbstractControllerTest {
	
	@Autowired 
	private SessionService sessionService;
	
	@Before
	public void setUp(){
		super.setUp();
		logger.info("Before test");
	}
	
	@After
	public void tearDown(){
		logger.info("After test");
	}
	
	@Test
	public void testGetSessions() throws Exception {
		String uri = "/api/sessions/";
		
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON)).andReturn();
		
		String content = result.getResponse().getContentAsString();		
		int status = result.getResponse().getStatus();
		
		assertThat(status).isEqualTo(200);
		assertThat(content.trim().length() > 0).isTrue();
	}
}
