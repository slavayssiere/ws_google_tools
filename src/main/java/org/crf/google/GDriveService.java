package org.crf.google;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crf.models.FileDrive;
import org.crf.models.Session;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GDriveService {
	GConnectToken gct;

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

    public Collection<FileDrive> test() throws Exception {
        // Build a new authorized API client service.
        Drive service = getDriveService();

        Map<String, FileDrive> ret = new HashMap<String, FileDrive>();
        
        FileList result = service.files().list()	       
	        .setFields("files(description,id,name,mimeType),nextPageToken")
	        .setPageSize(1000)
	        .execute();
        
        for(File file : result.getFiles()){
        	if(file.getMimeType().equals("application/vnd.google-apps.spreadsheet")){
	        	FileDrive fd = new FileDrive();
	        	fd.setId(file.getId());
	        	fd.setName(file.getName());
	        	ret.put(file.getId(), fd);
        	}
        }
        	        
        return ret.values();
    }
}
