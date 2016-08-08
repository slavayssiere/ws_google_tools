package org.crf.google;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.crf.models.FileDrive;
import org.crf.models.Session;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GDriveService {
	GConnectToken gct;
	SimpleDateFormat dt1 = new SimpleDateFormat("yyyy - MM - dd");

    public GDriveService(GConnectToken newgct) {
        gct = newgct;
    }    
    
    private Drive getDriveService() throws Exception {
        Credential credential = gct.authorize();
        return new Drive.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), credential)
                .setApplicationName(gct.getAPPLICATION_NAME())
                .build();
    }

    public Collection<FileDrive> findAll() throws Exception {
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
	
}
