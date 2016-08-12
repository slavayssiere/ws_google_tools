package org.crf.ws.services;

import org.crf.google.GoogleConnection;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.Oauth2.Userinfo;
import com.google.api.services.oauth2.model.Userinfoplus;

@Service
public class InfoUserServiceBean implements InfoUserService {
	GoogleConnection gct;


    /* (non-Javadoc)
	 * @see org.crf.google.UserInfo#setToken(org.crf.google.GConnectToken)
	 */
    @Override
	public void setToken(GoogleConnection newgct){
        gct = newgct;
    }
    
    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws Exception 
     */
    private Oauth2 getOauth2Service() throws Exception {
        Credential credential = gct.authorize();
        return new Oauth2.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), credential)
        		.setApplicationName(gct.getAPPLICATION_NAME())
        		.build();
    }
    
    /* (non-Javadoc)
	 * @see org.crf.google.UserInfo#getUserEmail()
	 */
    @Override
	public String getUserEmail() {
    	Oauth2 service = null;
    	Userinfoplus uinfo = null;
		try {
			service = getOauth2Service();
	    	uinfo = service.userinfo().get().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("connection email:" + uinfo.getEmail());
        return uinfo.getEmail();
    }

}
