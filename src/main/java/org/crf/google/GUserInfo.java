package org.crf.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.Oauth2.Userinfo;
import com.google.api.services.oauth2.model.Userinfoplus;

public class GUserInfo {
	GConnectToken gct;

    /** Constructor
     * 
     */
    public GUserInfo(GConnectToken newgct) {
        gct = newgct;
    }

    
    
    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws Exception 
     */
    public Oauth2 getOauth2Service() throws Exception {
        Credential credential = gct.authorize();
        return new Oauth2.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), credential)
        		.setApplicationName(gct.getAPPLICATION_NAME())
        		.build();
    }
    
    public String getUserEmail() {
    	Oauth2 service = null;
    	Userinfoplus uinfo = null;
		try {
			service = getOauth2Service();
	    	uinfo = service.userinfo().get().execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("connection email:" + uinfo.getEmail());
        return uinfo.getEmail();
    }

}
