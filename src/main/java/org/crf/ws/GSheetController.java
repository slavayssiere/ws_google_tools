package org.crf.ws;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.crf.google.GCalendarService;
import org.crf.google.GConnectToken;
import org.crf.google.GDriveService;
import org.crf.google.GScriptService;
import org.crf.google.GSheetService;
import org.crf.google.GUserInfo;
import org.crf.models.FileDrive;
import org.crf.models.Inscription;
import org.crf.models.ScriptData;
import org.crf.models.Session;
import org.mortbay.util.ajax.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

@RestController
public class GSheetController {

	@RequestMapping(value = "/api/sheets/state", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Session>> getStates(@RequestParam("token") String token) {

		List<Session> sessionColl = new ArrayList<Session>();
		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GDriveService gdriveserv = new GDriveService(gconnecttoken);
			GSheetService gss = new GSheetService(gconnecttoken);
			int id = 0;
			for (FileDrive file : gdriveserv.findAll()) {
				System.out.println("file:" + file.getName());
				Session sess = gss.getState(file.getId(), id);
				if (sess != null) {
					sess.setGoogle_id(file.getId());
					sess.setGoogle_name(file.getName());
					sessionColl.add(sess);
					id++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<List<Session>>(sessionColl, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/sheets/state", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Session> getState(@RequestParam("token") String token, @RequestBody Session sess) {
		
		Session newsess = null;
		GConnectToken gconnecttoken = new GConnectToken();
		SimpleDateFormat formaterTitle = new SimpleDateFormat("yyyy - MM - dd");
		String titleFile = formaterTitle.format(sess.getDate()) + " " + sess.getType();
		System.out.println(titleFile);
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GDriveService gdriveserv = new GDriveService(gconnecttoken);
			GSheetService gss = new GSheetService(gconnecttoken);
			int id = 0;
			FileDrive file = gdriveserv.findOne(titleFile);
			System.out.println("file:" + file.getName());
			newsess = gss.getState(file.getId(), id);
			if (newsess != null) {
				newsess.setGoogle_id(file.getId());
				newsess.setGoogle_name(file.getName());
				id++;
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<Session>(newsess, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/sheets/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Session> createNewSession(@RequestParam("token") String token, @RequestBody Session sess) {

		Session getsession = null;
		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GDriveService gdriveserv = new GDriveService(gconnecttoken);
			getsession = gdriveserv.copy("1TYxCseZaivEYMoWSPeYiF6zpLkKYPvDoY5t0aMWsMg0", sess);
			if(getsession==null){
				new ResponseEntity<Session>(getsession, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			GSheetService gss = new GSheetService(gconnecttoken);
			gss.update(getsession);
			GCalendarService gcs = new GCalendarService(gconnecttoken);
			gcs.create(getsession);
		} catch (GoogleJsonResponseException gjre){
			new ResponseEntity<Session>(getsession, HttpStatus.UNAUTHORIZED);
		} catch (IOException e) {
			e.printStackTrace();
			new ResponseEntity<Session>(getsession, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			new ResponseEntity<Session>(getsession, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Session>(getsession, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/api/sheets/launchscript", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ScriptData> launchScript(@RequestParam("token") String token) {

		ScriptData sr = new ScriptData();
		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GScriptService gss = new GScriptService(gconnecttoken);
			sr.setFunctionName("sendInscrit");
			sr.setScriptId("M1iqHwQ-j7dDT1NrEe_AKQvuRtcQ09sPP");
			sr.setSheetId("1p3KhsRyutUGy75DHygK_yfrUteW8bIyfQ9BIrd00XB8");
			sr = gss.execute(sr); // scriptid,sheetid
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

	//
	@RequestMapping(value = "/api/sheets/getemails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Inscription>> launchScriptGetEmails(@RequestParam("token") String token) {

		ScriptData sr = new ScriptData();
		List<Inscription> listreturn = null;

		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GScriptService gss = new GScriptService(gconnecttoken);
			sr.setFunctionName("listEmails");
			sr.setScriptId("MHr4dqf9ZmBeO9uaeqW8lPF8Gg9vJhx0I");
			sr.setSheetId("1zoE5UHWmZKljQFGqOBUgWGEikr1So9HuZnH4Y0td6XE");
			sr = gss.execute(sr); // scriptid,sheetid
			System.out.println(sr);
			if(sr.getError() != null){
				return new ResponseEntity<List<Inscription>>(listreturn, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			GSheetService gsheet = new GSheetService(gconnecttoken);
			listreturn = gsheet.getDataFromGetEmail();
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
	
	@RequestMapping(
			value = "/api/sheets/inscription/{sheetid}/{row}", 
			method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Inscription> getState(@RequestParam("token") String token, 
			@RequestBody Inscription inscr,
			@PathVariable("sheetid") String sheetid,
			@PathVariable("row") int row) {
		
		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GSheetService gss = new GSheetService(gconnecttoken);
			gss.addInscription(sheetid, row, inscr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<Inscription>(inscr, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/api/sheets/complete", 
			method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Inscription> getState(@RequestParam("token") String token, 
			@RequestBody Inscription inscr) {
		
		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GSheetService gss = new GSheetService(gconnecttoken);
			gss.addEmailNewDate(inscr);
			gss.addWaitingInscription(inscr);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<Inscription>(inscr, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/api/sheets/getemails/{row}", 
			method = RequestMethod.DELETE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> removeRowInGetEmails(@RequestParam("token") String token, 
			@PathVariable("row") int row) {
		
		boolean ret = false;
		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GSheetService gss = new GSheetService(gconnecttoken);
			ret = gss.deleteRow(row);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<Boolean>(ret, HttpStatus.OK);
	}
}
