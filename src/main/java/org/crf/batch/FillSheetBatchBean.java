package org.crf.batch;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.crf.google.GoogleConnectionBatch;
import org.crf.models.FileDrive;
import org.crf.models.Inscription;
import org.crf.models.ScriptData;
import org.crf.models.Session;
import org.crf.ws.services.DriveService;
import org.crf.ws.services.InfoUserService;
import org.crf.ws.services.ScriptService;
import org.crf.ws.services.SheetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

@Component
public class FillSheetBatchBean {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DriveService driveService;

	@Autowired
	private SheetService sheetService;

	@Autowired
	private ScriptService scriptService;

	// @Scheduled(cron = "0,30 * * * * *")
	@Scheduled(initialDelay = 5000, fixedDelay = 900000)
	public void updateFileSessionsAvailable() {
		logger.info("batch debut");

		int id = 1;
		try {
			GoogleConnectionBatch gcb = new GoogleConnectionBatch();

			driveService.setToken(gcb);
			sheetService.setToken(gcb);
			scriptService.setToken(gcb);

			sheetService.eraseSessionsDisponibles();

			for (FileDrive file : driveService.findAllAfter(new Date())) {
				try {
					Session sess = sheetService.getState(file.getId(), id);
					if (sess != null) {
						sess.setGoogle_id(file.getId());
						sess.setGoogle_name(file.getName());

						if (sess.getNbEmpty() > 0) {
							sheetService.updateSessionsDisponibles(sess.getDate(), sess.getNbEmpty(), id);
							id++;
						}
					}
				} catch (NullPointerException npe) {
					logger.info(npe.getMessage());
				}
			}
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("batch fin");
	}

	//@Scheduled(initialDelay=5000, fixedDelay=900000 )
	public void launchEmailsWithoutText(){
		logger.info("batch debut");

		int id = 1;
		try {
			GoogleConnectionBatch gcb = new GoogleConnectionBatch();
			

			driveService.setToken(gcb);
			sheetService.setToken(gcb);
			scriptService.setToken(gcb);
			
			ScriptData sr = new ScriptData();
			List<Inscription> listreturn = null;

			try {
				sr.setFunctionName("listEmails");
				sr.setScriptId("MHr4dqf9ZmBeO9uaeqW8lPF8Gg9vJhx0I");
				sr.setSheetId("1zoE5UHWmZKljQFGqOBUgWGEikr1So9HuZnH4Y0td6XE");
				sr.setNewArguments("blabla");
				sr = scriptService.execute(sr); // scriptid,sheetid
				if (sr.getError() != null) {
					logger.info(sr.getError());
				}
				listreturn = sheetService.getDataFromGetEmail();
				
				for(Inscription insc: listreturn){
					
					SimpleDateFormat formaterTitle = new SimpleDateFormat("yyyy - MM - dd");
					String titleFile = formaterTitle.format(insc.getDateFormation() + " " + insc.getTypeFormation());
					FileDrive file = driveService.findOne(titleFile);
					Session newsess = sheetService.getState(file.getId(), 0);
					if (newsess != null) {
						if(newsess.getNbEmpty()!=0){
							if(insc.getMessage()=="-----"){
								int row = newsess.getEmptyRows().get(0);
								sheetService.addInscription(file.getId(), row, insc);
								sheetService.deleteRow(row);
							} 
						}
						else {
							
						}
					}
				}
			} catch (GoogleJsonResponseException gjre) {
				gjre.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("batch fin");
	}
}
