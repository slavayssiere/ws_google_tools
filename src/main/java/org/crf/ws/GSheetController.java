package org.crf.ws;

import java.io.IOException;
import java.util.Collection;

import org.crf.google.GConnectToken;
import org.crf.google.GDriveService;
import org.crf.models.FileDrive;
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
	public ResponseEntity<Collection<FileDrive>> getCode(@RequestParam("token") String token) {	
		
		Collection<FileDrive> ret = null;
		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.setAccessToken(token);
			gconnecttoken.authorize();
			GDriveService gss = new GDriveService(gconnecttoken);
			ret = gss.test();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new ResponseEntity<Collection<FileDrive>>(ret, HttpStatus.OK);		
	}
}

