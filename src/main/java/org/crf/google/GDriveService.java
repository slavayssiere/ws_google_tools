package org.crf.google;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.crf.models.FileDrive;
import org.crf.models.Session;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GDriveService {
	GConnectToken gct;
	SimpleDateFormat dt1 = new SimpleDateFormat("yyyy - MM - dd");

    /** Constructor
     * 
     */
    public GDriveService(GConnectToken newgct) {
        gct = newgct;
    }    
    
    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws Exception 
     */
    public Drive getDriveService() throws Exception {
        Credential credential = gct.authorize();
        return new Drive.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), credential)
                .setApplicationName(gct.getAPPLICATION_NAME())
                .build();
    }

    public Collection<FileDrive> getListSheet() throws Exception {
        // Build a new authorized API client service.
        Drive service = getDriveService();

        Map<String, FileDrive> ret = new HashMap<String, FileDrive>();   
        
        
        FileList result =  service.files().list()	       
	        .setFields("files(id,name,mimeType),nextPageToken")
	        .setQ("mimeType='application/vnd.google-apps.spreadsheet' and name contains '2016'")
	        .setPageSize(1000)
	        .execute();
        
        for(File file : result.getFiles()){
        	FileDrive fd = new FileDrive();
        	fd.setId(file.getId());
        	fd.setName(file.getName());
        	ret.put(file.getId(), fd);        	
        }
        	        
        return ret.values();
    }

	public Session copy(String fileId, Session sess) throws Exception {
		Drive service = getDriveService();
		
		String copyTitle = dt1.format(sess.getDate()) +  " " + sess.getType() + " " + sess.getFormateur();
		File copiedFile = new File();
	    copiedFile.setName(copyTitle);
	    
		File newfile = service.files().copy(fileId, copiedFile).execute();
		
		sess.setGoogle_id(newfile.getId());
		sess.setGoogle_name(copyTitle);
		
		return sess;
	}

	public FileDrive getSheetByName(String titleFile) throws Exception {
		Drive service = getDriveService();

        Map<String, FileDrive> ret = new HashMap<String, FileDrive>();   
        
        
        FileList result = service.files().list()	       
	        .setFields("files(id,name,mimeType),nextPageToken")
	        .setQ("mimeType='application/vnd.google-apps.spreadsheet' and name contains '"+titleFile+"'")
	        .setPageSize(1000)
	        .execute();
        
        File file = result.getFiles().get(0);
    	FileDrive fd = new FileDrive();
    	fd.setId(file.getId());
    	fd.setName(file.getName());    
        	        
        return fd;
	}
	
}
