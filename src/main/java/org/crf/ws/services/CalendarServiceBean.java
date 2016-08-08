package org.crf.ws.services;

import java.util.Date;
import java.util.TimeZone;

import org.crf.google.GConnectToken;
import org.crf.models.Session;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

@Service
public class CalendarServiceBean implements CalendarService {
	private GConnectToken gct;

	@Override
    public void setToken(GConnectToken newgct){
        gct = newgct;
    }
    
    private Calendar getCalendarService() throws Exception {
        Credential credential = gct.authorize();
        return new Calendar.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), credential)
        		.setApplicationName(gct.getAPPLICATION_NAME())
        		.build();
    }
    
    /* (non-Javadoc)
	 * @see org.crf.google.CalendarService#create(org.crf.models.Session)
	 */
    @Override
	public Event create(Session sess) throws Exception {
    	Calendar service = getCalendarService();
    	String calendarPSC1 = "udk2esse0hsos1ho8a7ukng43s@group.calendar.google.com";
    	String calendarIPSEN = "os1qv8all2n00vg5l80qu0brog@group.calendar.google.com";
    	String calendarId = "";
    	if(sess.getType().equals("PSC1")){
    		calendarId = calendarPSC1;
    	}
    	else {
    		calendarId = calendarIPSEN;
    	}
    	
    	Event event = new Event();
        event.setSummary(sess.getType() + " " + sess.getFormateur());
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(sess.getDate());
        cal.set(java.util.Calendar.HOUR_OF_DAY, sess.getHeure());
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        
        Date startDate = cal.getTime();
        cal.add(java.util.Calendar.HOUR, 9);
        Date endDate = cal.getTime();
        
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("Europe/Paris"));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("Europe/Paris"));
        
        event.setStart(new EventDateTime().setDateTime(start));
        event.setEnd(new EventDateTime().setDateTime(end));
    	
		return service.events().insert(calendarId, event).execute();
    	
    }
}
