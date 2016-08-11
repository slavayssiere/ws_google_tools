package org.crf.ws.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.crf.AbstractTest;
import org.crf.models.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class SessionServiceTest extends AbstractTest {
	
	@Autowired
	private SessionService sessionService;
	
	@Before
	public void setUp(){
		logger.info("Before test");		
	}
	
	@After
	public void tearDown(){
		logger.info("after test");
	}
	
	@Test
	public void testFindAll(){
		Collection<Session> list = sessionService.findAll();
		
		assertThat(list.size()).isEqualTo(2);
		assertThat(list).isNotNull();
	}
}
