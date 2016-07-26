package org.crf.google;

import java.text.SimpleDateFormat;
import java.util.List;

import org.crf.models.Session;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

public class GSheetService {
	
	GConnectToken gct;

    /** Constructor
     * 
     */
    public GSheetService(GConnectToken newgct) {
        gct = newgct;
    }

    
    
    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws Exception 
     */
    public Sheets getSheetsService() throws Exception {
        Credential credential = gct.authorize();
        return new Sheets.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), credential)
                .setApplicationName(gct.getAPPLICATION_NAME())
                .build();
    }

    public Session getState(String spreadsheetId) throws Exception {
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        Session sess = new Session();
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
        
        // Prints the names and majors of students in a sample spreadsheet:
        // https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
        ValueRange response = service.spreadsheets().values()        	
            .get(spreadsheetId, "Admin!B1:E1")
            .setFields("properties/title,sheets/data/rowData/values/formattedValue")
            .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.size() == 0) {
            return null;
        } else {
          System.out.println("Name, Major");
          for (List row : values) {
            sess.setFormateur((String)row.get(3));
            sess.setDate(formater.parse((String) row.get(1)));
            sess.setType((String) row.get(0));
            
            System.out.println(row.get(0) + " " + row.get(1) + " " + row.get(3));
          }
        }
        return sess;
    }
}
