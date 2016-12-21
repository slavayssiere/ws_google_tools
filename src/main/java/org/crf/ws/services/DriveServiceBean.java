package org.crf.ws.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.crf.google.GoogleConnection;
import org.crf.models.FileDrive;
import org.crf.models.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

@Service
public class DriveServiceBean implements DriveService {
	GoogleConnection gct = null;
	SimpleDateFormat dt1 = new SimpleDateFormat("yyyy - MM - dd");
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
    public void setToken(GoogleConnection newgct){
        gct = newgct;
    }  
    
    private Drive getDriveService() throws Exception {
        Credential credential = gct.authorize();
        return new Drive.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), credential)
                .setApplicationName(gct.getAPPLICATION_NAME())
                .build();
    }

    /* (non-Javadoc)
	 * @see org.crf.google.DriveService#findAll()
	 */
    @Override
	public Collection<FileDrive> findAll(String year) throws Exception {
        // Build a new authorized API client service.
        Drive service = getDriveService();

        Map<String, FileDrive> ret = new HashMap<String, FileDrive>();   
        
        
        FileList result =  service.files().list()	       
	        .setFields("files(id,name,mimeType),nextPageToken")
	        .setQ("mimeType='application/vnd.google-apps.spreadsheet' and name contains '"+year+"'")
	        .setPageSize(1000)
	        .execute();
        
        for(File file : result.getFiles()){
        	FileDrive fd = new FileDrive();
        	if(file.getName().contains("ANNULE")==false){
	        	fd.setId(file.getId());
	        	fd.setName(file.getName());
	        	ret.put(file.getId(), fd);        	
        	}
        }
        	        
        return ret.values();
    }

	/* (non-Javadoc)
	 * @see org.crf.google.DriveService#copy(java.lang.String, org.crf.models.Session)
	 */
	@Override
	public Session copy(String fileId, Session sess) throws GoogleJsonResponseException {
		Drive service;
		try {
			service = getDriveService();
		} catch (GoogleJsonResponseException gjre) {
			throw gjre;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		String copyTitle = dt1.format(sess.getDate()) +  " " + sess.getType() + " " + sess.getFormateur();
		File copiedFile = new File();
	    copiedFile.setName(copyTitle);
	    
		File newfile;
		try {
			newfile = service.files().copy(fileId, copiedFile).execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		sess.setGoogle_id(newfile.getId());
		sess.setGoogle_name(copyTitle);
		
		return sess;
	}

	/* (non-Javadoc)
	 * @see org.crf.google.DriveService#findOne(java.lang.String)
	 */
	@Override
	public FileDrive findOne(String titleFile) throws Exception {
		Drive service = getDriveService();

        Map<String, FileDrive> ret = new HashMap<String, FileDrive>();   
        
        
        FileList result = service.files().list()	       
	        .setFields("files(id,name,mimeType),nextPageToken")
	        .setQ("mimeType='application/vnd.google-apps.spreadsheet' and name contains '"+titleFile+"'")
	        .setPageSize(1000)
	        .execute();
        
        System.out.println(result.getFiles());
        
        FileDrive fd = null;
        for(File file : result.getFiles()){
        	fd = new FileDrive();
        	if(file.getName().contains(titleFile)){
	        	fd.setId(file.getId());
	        	fd.setName(file.getName());
	        	break;
        	}
        }
        
           
        	        
        return fd;
	}

	@Override
	public Collection<FileDrive> findAllAfter(Date date) throws Exception {
		// Build a new authorized API client service.
        Drive service = getDriveService();

        Map<String, FileDrive> ret = new HashMap<String, FileDrive>();   
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        FileList result =  service.files().list()	       
	        .setFields("files(id,name,mimeType),nextPageToken")
	        .setQ("mimeType='application/vnd.google-apps.spreadsheet' and name contains '"+cal.get(Calendar.YEAR)+"' or name contains '"+(cal.get(Calendar.YEAR) + 1)+"'")
	        .setPageSize(1000)
	        .execute();
        
        for(File file : result.getFiles()){
        	FileDrive fd = new FileDrive();
        	if(file.getName().contains("ANNULE")==false){
        		
        		String[] test = file.getName().split(" ");
        		
        		try
        		{
	        		cal.set(Calendar.YEAR, Integer.parseInt(test[0]));
	        		cal.set(Calendar.MONTH, Integer.parseInt(test[2]) - 1);
	        		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(test[4]));
	        		
	        		if(cal.getTime().after(date)){
			        	fd.setId(file.getId());
			        	fd.setName(file.getName());
			        	ret.put(file.getId(), fd); 
	        		}
        		}
        		catch(Exception e)
        		{
        			logger.info("Filename in error: " + file.getName());
        		}
        	}
        }
        	        
        return ret.values();
	}
	
}
