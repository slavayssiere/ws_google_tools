package org.crf.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.crf.google.GConnectToken;
import org.crf.google.GDriveService;
import org.crf.google.GSheetService;
import org.crf.models.FileDrive;
import org.crf.models.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GSheetController {

	@RequestMapping(
			value="/api/files", 
			method=RequestMethod.GET, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<FileDrive>> getSheets(@RequestParam("token") String token) {	
		
		Collection<FileDrive> ret = null;
		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GDriveService gss = new GDriveService(gconnecttoken);
			ret = gss.getListSheet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new ResponseEntity<Collection<FileDrive>>(ret, HttpStatus.OK);		
	}
	
	@RequestMapping(
			value="/api/sheets/state", 
			method=RequestMethod.GET, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Session>> getStates(@RequestParam("token") String token) {	
		
		List<Session> sessionColl = new ArrayList<Session>();
		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GDriveService gdriveserv = new GDriveService(gconnecttoken);
			int id = 0;
			for(FileDrive file : gdriveserv.getListSheet()){
				//System.out.println("file:" + file.getName());
				//System.out.println("fileid:" + file.getId());
				GSheetService gss = new GSheetService(gconnecttoken);
				Session sess = gss.getState(file.getId(), id);
				if(sess != null){
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
}

