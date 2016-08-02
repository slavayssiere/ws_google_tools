package org.crf.google;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crf.models.Inscription;
import org.crf.models.Session;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.util.concurrent.ExecutionList;

public class GSheetService {

	GConnectToken gct;

	/**
	 * Constructor
	 * 
	 */
	public GSheetService(GConnectToken newgct) {
		gct = newgct;
	}

	/**
	 * Build and return an authorized Sheets API client service.
	 * 
	 * @return an authorized Sheets API client service
	 * @throws Exception
	 */
	private Sheets getSheetsService() throws Exception {
		Credential credential = gct.authorize();
		return new Sheets.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), credential)
				.setApplicationName(gct.getAPPLICATION_NAME()).build();
	}

	SimpleDateFormat formaterHyphen = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat formaterSlash = new SimpleDateFormat("dd/MM/yyyy");

	public Session getState(String spreadsheetId, int id) throws Exception {
		Sheets service = getSheetsService();
		Session sess = new Session();

		ValueRange response;
		try {
			response = service.spreadsheets().values().get(spreadsheetId, "Admin!A1:F18")
					// .setFields("properties/title,sheets/data/rowData/values/formattedValue")
					.execute();
		} catch (GoogleJsonResponseException gje) {
			// gje.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		List<List<Object>> values = response.getValues();
		if (values == null || values.size() == 0) {
			System.out.println("null no data");
			return null;
		} else {
			int nbrow = 0;
			for (List row : values) {
				if (nbrow == 0) {
					sess.setFormateur((String) row.get(4));
					try {
						sess.setDate(formaterHyphen.parse((String) row.get(2)));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						sess.setDate(formaterSlash.parse((String) row.get(2)));
					}
					sess.setType((String) row.get(1));
					sess.setId(id);
				} else if (nbrow >= 5) {
					if (row.size() == 6) {
						try {
							Inscription insc = new Inscription();
							insc.setPresence((String) row.get(1));
							insc.setReglement((String) row.get(2));
							insc.setCivilite((String) row.get(3));
							insc.setPrenom((String) row.get(4));
							insc.setNom((String) row.get(5));

							sess.addInscription(insc);
						} catch (IndexOutOfBoundsException iob) {
							iob.printStackTrace();
						}
					} else if (row.size() == 1) {
						sess.addEmptyRow(nbrow);
					} else {
						System.out.println(spreadsheetId + " " + nbrow + " " + row.toString());
					}
				}
				nbrow++;
			}
		}
		return sess;
	}

	public void updateNewSheet(Session sess) throws Exception {
		Sheets service = getSheetsService();

		List<Request> requests = new ArrayList<>();
		List<CellData> values = new ArrayList<>();
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(sess.getType())));
		values.add(new CellData()
				.setUserEnteredValue(new ExtendedValue().setStringValue(this.formaterHyphen.format(sess.getDate()))));
		values.add(new CellData()
				.setUserEnteredValue(new ExtendedValue().setNumberValue(Double.valueOf(sess.getHeure()))));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(sess.getFormateur())));
		requests.add(new Request().setUpdateCells(
				new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(0).setRowIndex(0).setColumnIndex(1))
						.setRows(Arrays.asList(new RowData().setValues(values))).setFields("userEnteredValue")));

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);

		service.spreadsheets().batchUpdate(sess.getGoogle_id(), batchUpdateRequest).execute();
	}

	public List<Inscription> getDataFromGetEmail(int nombreInscrit) throws Exception {
		List<Inscription> listEmails = new ArrayList<Inscription>();
		Sheets service = getSheetsService();
		String spreadsheetId = "1zoE5UHWmZKljQFGqOBUgWGEikr1So9HuZnH4Y0td6XE";

		ValueRange response;
		try {
			response = service.spreadsheets().values().get(spreadsheetId, "getEmails!A2:M" + (nombreInscrit + 1))
					.execute();
		} catch (GoogleJsonResponseException gje) {
			// gje.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		List<List<Object>> values = response.getValues();
		if (values == null || values.size() == 0) {
			System.out.println("null no data");
			return null;
		} else {
			for (List row : values) {
				try {

					Inscription insc = new Inscription();
					insc.setTypeFormation((String) row.get(0));
					insc.setDateFormation(formaterSlash.parse((String) row.get(1)));
					insc.setCivilite((String) row.get(2));
					insc.setPrenom((String) row.get(3));
					insc.setNom((String) row.get(4));
					insc.setDatenaissance((String) row.get(5));
					insc.setLieunaissance((String) row.get(6));
					insc.setAdresse((String) row.get(7));
					insc.setCodepostal((String) row.get(8));
					insc.setVille((String) row.get(9));
					insc.setPhone((String) row.get(10));
					insc.setEmail((String) row.get(11));
					insc.setMessage((String) row.get(12));

					listEmails.add(insc);
				} catch (IndexOutOfBoundsException iob) {
					System.out.println("error " + row.toString());
					iob.printStackTrace();
				} catch (ParseException pe) {
					System.out.println("error " + row.toString());
					pe.printStackTrace();
				}
			
			}
		}
		return listEmails;
	}

	public void addInscription(String sheetid, int row, Inscription inscr) throws Exception {
		Sheets service = getSheetsService();

		List<Request> requests = new ArrayList<>();
		List<CellData> values = new ArrayList<>();
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getCivilite())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getPrenom())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getNom())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getDatenaissance())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getLieunaissance())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getAdresse())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getCodepostal())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getVille())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getPhone())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getEmail())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getMessage())));
		
		requests.add(new Request().setUpdateCells(
				new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(0).setRowIndex(row).setColumnIndex(3))
						.setRows(Arrays.asList(new RowData().setValues(values))).setFields("userEnteredValue")));

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);

		service.spreadsheets().batchUpdate(sheetid, batchUpdateRequest).execute();		
	}
	
	public void addEmailNewDate(Inscription inscr) throws Exception {
		Sheets service = getSheetsService();
		String spreadsheetId = "1dgM6JG5GOc72B5a2ZdGlcIDYw9XPNV75cDB9h5e17vQ";
		List<Request> requests = new ArrayList<>();
		List<CellData> values = new ArrayList<>();
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getEmail())));
		
		String sheetName = "";
		if(inscr.getTypeFormation().equals("PSC1")){
			sheetName = "PSC1complet";
		}
		else{
			sheetName = "IPSENcomplet";
		}
		
		int row = 1;
		
		ValueRange response = null;
		try {
			response = service.spreadsheets().values().get(spreadsheetId, sheetName + "!A1:A60")
					.execute();
		} catch (GoogleJsonResponseException gje) {
			gje.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<List<Object>> valuesCell = response.getValues();
		if (valuesCell == null || valuesCell.size() == 0) {
			System.out.println("null no data");
		} else {
			row=valuesCell.size();
		}
		
		requests.add(new Request().setUpdateCells(
				new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(0).setRowIndex(row).setColumnIndex(0))
						.setRows(Arrays.asList(new RowData().setValues(values))).setFields("userEnteredValue")));

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);

		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();		
	}
}
