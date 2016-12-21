package org.crf.ws;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.crf.google.GoogleConnectionBean;
import org.crf.google.GoogleConnection;
import org.crf.models.FileDrive;
import org.crf.models.Session;
import org.crf.ws.services.CalendarService;
import org.crf.ws.services.DriveService;
import org.crf.ws.services.SheetService;
import org.crf.ws.services.SheetServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/sheets")
@Api(value="/api/sheets" , description="Session in gsheet management", consumes="application/json")
public class InscriptionController extends BaseController {

	@Autowired
	private CalendarService calendarService;
	
	@Autowired
	private DriveService driveService;
	
	@Autowired
	private SheetService sheetService;
	
	@Autowired
	private GoogleConnection gconnect;

	@RequestMapping(value = "/state", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="GetAllSessions", nickname="Get all sessions")
	public ResponseEntity<List<Session>> getStates(@RequestParam("token") String token) {

		List<Session> sessionColl = new ArrayList<Session>();
		try {
			gconnect.setAccessToken(token);
			driveService.setToken(gconnect);
			sheetService.setToken(gconnect);
			
			int id = 0;
			for (FileDrive file : driveService.findAllAfter(new Date())) {
				Session sess = sheetService.getState(file.getId(), id);
				if (sess != null) {
					sess.setGoogle_id(file.getId());
					sess.setGoogle_name(file.getName());
					sessionColl.add(sess);
					id++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			new ResponseEntity<List<Session>>(sessionColl, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			new ResponseEntity<List<Session>>(sessionColl, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<Session>>(sessionColl, HttpStatus.OK);
	}

	@RequestMapping(value = "/state", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="GetDataForOneSession", nickname="Get Data For One Session")
	public ResponseEntity<Session> getState(@RequestParam("token") String token, @RequestBody Session sess) {

		Session newsess = null;
		SimpleDateFormat formaterTitle = new SimpleDateFormat("yyyy - MM - dd");
		String titleFile = formaterTitle.format(sess.getDate()) + " " + sess.getType();
		try {
			gconnect.setAccessToken(token);
			driveService.setToken(gconnect);
			sheetService.setToken(gconnect);
			int id = 0;
			FileDrive file = driveService.findOne(titleFile);
			System.out.println("file:" + file.getName());
			newsess = sheetService.getState(file.getId(), id);
			if (newsess != null) {
				newsess.setGoogle_id(file.getId());
				newsess.setGoogle_name(file.getName());
				id++;

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Session>(newsess, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Session>(newsess, HttpStatus.OK);
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="CreateNewSession", nickname="Create new session in calendar and sheet")
	public ResponseEntity<Session> createNewSession(@RequestParam("token") String token, @RequestBody Session sess) {

		Session getsession = null;
		try {
			gconnect.setAccessToken(token);
			driveService.setToken(gconnect);
			sheetService.setToken(gconnect);
			calendarService.setToken(gconnect);
			
			getsession = driveService.copy("1TYxCseZaivEYMoWSPeYiF6zpLkKYPvDoY5t0aMWsMg0", sess);
			if (getsession == null) {
				new ResponseEntity<Session>(getsession, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			sheetService.update(getsession);
			calendarService.create(getsession);
			
		} catch (GoogleJsonResponseException gjre) {
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

}
