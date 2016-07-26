package org.crf.ws;

import java.io.IOException;

import org.crf.google.GConnectToken;
import org.crf.models.GoogleCodeFlow;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnexionController {

	@RequestMapping(
			value="/api/auth/google", 
			method=RequestMethod.POST, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GConnectToken> getCode(@RequestBody GoogleCodeFlow data) {	
		
		GConnectToken gconnecttoken = new GConnectToken();
		try {
			gconnecttoken.token_create(data.getCode(), data.getRedirectUri());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ResponseEntity<GConnectToken>(gconnecttoken, HttpStatus.OK);		
	}
}