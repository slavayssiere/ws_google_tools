package org.crf.ws;

import java.io.IOException;
import java.util.List;

import org.crf.google.GoogleConnectionBean;
import org.crf.google.GoogleConnection;
import org.crf.models.Inscription;
import org.crf.models.ScriptData;
import org.crf.ws.services.DriveService;
import org.crf.ws.services.GmailService;
import org.crf.ws.services.ScriptService;
import org.crf.ws.services.ScriptServiceBean;
import org.crf.ws.services.SheetService;
import org.crf.ws.services.SheetServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

@RestController
public class GetEmailsController extends BaseController {
	
	@Autowired
	private ScriptService scriptService;
	
	@Autowired
	private SheetService sheetService;

	@Autowired
	private GoogleConnection gconnect;
	
	@Autowired
	private GmailService gmailService;
	
	@RequestMapping(value = "/api/sheets/launchscript", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ScriptData> launchScript(@RequestParam("token") String token) {

		ScriptData sr = new ScriptData();
		try {
			gconnect.setAccessToken(token);
			scriptService.setToken(gconnect);
			sr.setFunctionName("sendInscrit");
			sr.setScriptId("M1iqHwQ-j7dDT1NrEe_AKQvuRtcQ09sPP");
			sr.setSheetId("1p3KhsRyutUGy75DHygK_yfrUteW8bIyfQ9BIrd00XB8");
			sr = scriptService.execute(sr); // scriptid,sheetid
		} catch (GoogleJsonResponseException gjre) {
			gjre.printStackTrace();
			return new ResponseEntity<ScriptData>(sr, HttpStatus.UNAUTHORIZED);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<ScriptData>(sr, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<ScriptData>(sr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<ScriptData>(sr, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/sheets/getemails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Inscription>> launchScriptGetEmails(@RequestParam("token") String token) {

		ScriptData sr = new ScriptData();
		List<Inscription> listreturn = null;

		try {
			gconnect.setAccessToken(token);
			scriptService.setToken(gconnect);
			sheetService.setToken(gconnect);
			
			sr.setFunctionName("listEmails");
			sr.setScriptId("MHr4dqf9ZmBeO9uaeqW8lPF8Gg9vJhx0I");
			sr.setSheetId("1zoE5UHWmZKljQFGqOBUgWGEikr1So9HuZnH4Y0td6XE");
			sr.setNewArguments("blabla");
			sr = scriptService.execute(sr); // scriptid,sheetid
			if (sr.getError() != null) {
				return new ResponseEntity<List<Inscription>>(listreturn, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			listreturn = sheetService.getDataFromGetEmail();
		} catch (GoogleJsonResponseException gjre) {
			gjre.printStackTrace();
			return new ResponseEntity<List<Inscription>>(listreturn, HttpStatus.UNAUTHORIZED);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<List<Inscription>>(listreturn, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<Inscription>>(listreturn, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<Inscription>>(listreturn, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/sheets/inscription/{sheetid}/{row}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Inscription> getState(@RequestParam("token") String token, @RequestBody Inscription inscr,
			@PathVariable("sheetid") String sheetid, @PathVariable("row") int row) {

		try {
			gconnect.setAccessToken(token);
			sheetService.setToken(gconnect);
			sheetService.addInscription(sheetid, row, inscr);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<Inscription>(inscr, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Inscription>(inscr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Inscription>(inscr, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/sheets/complete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Inscription> getState(@RequestParam("token") String token, @RequestBody Inscription inscr) {

		try {
			gconnect.setAccessToken(token);
			sheetService.setToken(gconnect);
			sheetService.addEmailNewDate(inscr);
			sheetService.addWaitingInscription(inscr);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<Inscription>(inscr, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Inscription>(inscr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Inscription>(inscr, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/sheets/getemails/{row}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> removeRowInGetEmails(@RequestParam("token") String token,
			@PathVariable("row") int row) {

		boolean ret = false;
		try {
			gconnect.setAccessToken(token);
			sheetService.setToken(gconnect);
			ret = sheetService.deleteRow(row);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<Boolean>(ret, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Boolean>(ret, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Boolean>(ret, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/api/sheets/{sheetid}/sendinscrits", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> launchScriptEmailsInscrit(@RequestParam("token") String token, @PathVariable("sheetid") String sheetid) {

		ScriptData sr = new ScriptData();

		try {
			gconnect.setAccessToken(token);
			scriptService.setToken(gconnect);
			sr.setFunctionName("sendInscrit");
			sr.setScriptId("M1iqHwQ-j7dDT1NrEe_AKQvuRtcQ09sPP");
			sr.setSheetId("1p3KhsRyutUGy75DHygK_yfrUteW8bIyfQ9BIrd00XB8");
			sr.setNewArguments(sheetid);
			sr = scriptService.execute(sr); // scriptid,sheetid			
		} catch (GoogleJsonResponseException gjre) {
			gjre.printStackTrace();
			return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/sheets/draft", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> createDraft(@RequestParam("token") String token, @RequestBody Inscription inscr) {

		try {
			gconnect.setAccessToken(token);
			gmailService.setToken(gconnect);
			gmailService.createDraft(inscr);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
}
