package org.crf.batch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.crf.google.GConnectToken;
import org.crf.models.ScriptData;
import org.crf.ws.services.InfoUserService;
import org.crf.ws.services.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.script.ScriptScopes;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.oauth2.Oauth2Scopes;

@Component
public class GetEmailsBatchBean {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ScriptService scriptservice;
	
	@Autowired
	private InfoUserService userService;
	
	@Scheduled(cron = "0,30 * * * * *")
	public void cronJob(){
		GConnectToken newgct = new GConnectToken();
		List<String> scopes = new ArrayList<String>();
		scopes.add(SheetsScopes.SPREADSHEETS);
		scopes.add(ScriptScopes.MAIL_GOOGLE_COM);
		scopes.add(ScriptScopes.SPREADSHEETS);
		scopes.add(ScriptScopes.DRIVE);
		scopes.add(DriveScopes.DRIVE_SCRIPTS);
		scopes.add(Oauth2Scopes.USERINFO_EMAIL);
		ScriptData sr = new ScriptData();
		sr.setFunctionName("listEmails");
		sr.setScriptId("MHr4dqf9ZmBeO9uaeqW8lPF8Gg9vJhx0I");
		sr.setSheetId("1zoE5UHWmZKljQFGqOBUgWGEikr1So9HuZnH4Y0td6XE");
		
		logger.info("batch launch");
		
		try {
			newgct.createCredentialForServer(scopes);
			scriptservice.setToken(newgct);
			userService.setToken(newgct);
			logger.info(userService.getUserEmail());
			scriptservice.execute(sr);
		} catch (GoogleJsonResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("batch end");
		
	}
}
