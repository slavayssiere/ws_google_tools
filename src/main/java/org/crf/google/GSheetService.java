package org.crf.google;

import java.util.List;

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

    public String test() throws Exception {
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        String ret = "";
        // Prints the names and majors of students in a sample spreadsheet:
        // https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
        String spreadsheetId = "1MgNlnOiQInKaqiwZwaZ1tRxd7gKEON6xLAZR0PRBB6I";
        String range = "Feuille 1!A1:B10";
        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.size() == 0) {
            System.out.println("No data found.");
        } else {
          System.out.println("Name, Major");
          for (List row : values) {
            // Print columns A and E, which correspond to indices 0 and 4.
            ret = row.get(0) + " " + row.get(1);
          }
        }
        return ret;
    }
}
