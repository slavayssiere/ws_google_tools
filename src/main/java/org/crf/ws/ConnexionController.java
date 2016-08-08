package org.crf.ws;

import java.io.IOException;

import org.crf.google.GConnectToken;
import org.crf.models.GoogleCodeFlow;
import org.crf.ws.services.InfoUserService;
import org.crf.ws.services.InfoUserServiceBean;
import org.crf.ws.services.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnexionController extends BaseController {

	@Autowired
	private InfoUserService infoService;
	
	@Autowired
	private CounterService counterService;
	
	@RequestMapping(
			value="/api/auth/google", 
			method=RequestMethod.POST, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GConnectToken> getCode(@RequestBody GoogleCodeFlow data) {	
		
		counterService.increment("method.connexion");
		
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
		

		infoService.setToken(gconnecttoken);
		if (!infoService.getUserEmail().equals("inscription.crf7511@gmail.com")) {
			return new ResponseEntity<GConnectToken>(new GConnectToken(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<GConnectToken>(gconnecttoken, HttpStatus.OK);		
	}
}
