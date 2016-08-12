package org.crf.ws.services;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.crf.google.GoogleConnection;
import org.crf.models.Inscription;
import org.crf.models.Session;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.NumberFormat;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

@Service
public class SheetServiceBean implements SheetService {

	private GoogleConnection gct;

	private SimpleDateFormat formaterHyphen = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat formaterSlash = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat formaterGSheet = new SimpleDateFormat("yyyy-MM-dd");

	// Precomputed difference between the Unix epoch and the Sheets epoch.
	private final long SHEETS_EPOCH_DIFFERENCE = 2209161600000L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crf.google.SheetService#setToken(org.crf.google.GConnectToken)
	 */
	@Override
	public void setToken(GoogleConnection newgct) {
		gct = newgct;
	}

	private Sheets sheetsService = null;

	/**
	 * Build and return an authorized Sheets API client service.
	 * 
	 * @return an authorized Sheets API client service
	 * @throws Exception
	 */
	private Sheets getSheetsService() throws Exception {
		if (this.sheetsService == null) {
			Credential credential = gct.authorize();
			return new Sheets.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), credential)
					.setApplicationName(gct.getAPPLICATION_NAME()).build();
		}
		else {
			return this.sheetsService;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crf.google.SheetService#getState(java.lang.String, int)
	 */
	@Override
	public Session getState(String spreadsheetId, int id) throws Exception {
		Sheets service = getSheetsService();
		Session sess = new Session();

		ValueRange response;
		try {
			response = service.spreadsheets().values().get(spreadsheetId, "Admin!A1:F18")
					//.setFields("sheets/data/rowData/values/formattedValue")
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
						try {
							sess.setDate(formaterSlash.parse((String) row.get(2)));
						} catch (ParseException pe) {
							Date date = new Date((long) row.get(2));
							sess.setDate(date);
						}
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

	private double getEpochDate(Date inputDate) {
		long millisSinceUnixEpoch = inputDate.getTime();
		long millisSinceSheetsEpoch = millisSinceUnixEpoch + SHEETS_EPOCH_DIFFERENCE;
		return millisSinceSheetsEpoch / (double) TimeUnit.DAYS.toMillis(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crf.google.SheetService#update(org.crf.models.Session)
	 */
	@Override
	public void update(Session sess) throws Exception {
		Sheets service = getSheetsService();

		Double valDate = getEpochDate(sess.getDate());

		List<Request> requests = new ArrayList<>();
		List<CellData> values = new ArrayList<>();
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(sess.getType())));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setNumberValue(valDate))
				.setUserEnteredFormat(new CellFormat().setNumberFormat(new NumberFormat().setType("DATE"))));
		values.add(new CellData()
				.setUserEnteredValue(new ExtendedValue().setNumberValue(Double.valueOf(sess.getHeure()))));
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(sess.getFormateur())));

		requests.add(new Request().setUpdateCells(
				new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(0).setRowIndex(0).setColumnIndex(1))
						.setRows(Arrays.asList(new RowData().setValues(values)))
						.setFields("userEnteredValue,userEnteredFormat.numberFormat")));

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);

		service.spreadsheets().batchUpdate(sess.getGoogle_id(), batchUpdateRequest).execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crf.google.SheetService#getDataFromGetEmail()
	 */
	@Override
	public List<Inscription> getDataFromGetEmail() throws Exception {
		List<Inscription> listEmails = new ArrayList<Inscription>();
		Sheets service = getSheetsService();
		String spreadsheetId = "1zoE5UHWmZKljQFGqOBUgWGEikr1So9HuZnH4Y0td6XE";

		ValueRange response;
		try {
			response = service.spreadsheets().values().get(spreadsheetId, "getEmails!A2:M200").execute();
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
			int numRow = 2;
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

					insc.setRow(numRow);
					numRow++;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crf.google.SheetService#deleteRow(int)
	 */
	@Override
	public boolean deleteRow(int numRow) throws Exception {
		Sheets service = getSheetsService();
		String spreadsheetId = "1zoE5UHWmZKljQFGqOBUgWGEikr1So9HuZnH4Y0td6XE";

		List<Request> requests = new ArrayList<>();

		try {
			requests.add(new Request().setDeleteDimension(new DeleteDimensionRequest().setRange(new DimensionRange()
					.setSheetId(0).setDimension("ROWS").setStartIndex(numRow - 1).setEndIndex(numRow))));

			BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
					.setRequests(requests);
			service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crf.google.SheetService#addInscription(java.lang.String, int,
	 * org.crf.models.Inscription)
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crf.google.SheetService#addWaitingInscription(org.crf.models.
	 * Inscription)
	 */
	@Override
	public void addWaitingInscription(Inscription inscr) throws Exception {
		String spreadsheetId = "1zoE5UHWmZKljQFGqOBUgWGEikr1So9HuZnH4Y0td6XE";

		Sheets service = getSheetsService();
		int row = 1;

		ValueRange response = null;
		try {
			response = service.spreadsheets().values().get(spreadsheetId, "PSC1 Nouvelle date" + "!A1:A1000").execute();
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
			row = valuesCell.size();
		}

		List<Request> requests = new ArrayList<>();
		List<CellData> values = new ArrayList<>();
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getTypeFormation())));
		values.add(new CellData().setUserEnteredValue(
				new ExtendedValue().setStringValue(formaterSlash.format(inscr.getDateFormation()))));
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

		requests.add(new Request().setUpdateCells(new UpdateCellsRequest()
				.setStart(new GridCoordinate().setSheetId(1356295922).setRowIndex(row).setColumnIndex(0))
				.setRows(Arrays.asList(new RowData().setValues(values))).setFields("userEnteredValue")));

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);

		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.crf.google.SheetService#addEmailNewDate(org.crf.models.Inscription)
	 */
	@Override
	public void addEmailNewDate(Inscription inscr) throws Exception {
		Sheets service = getSheetsService();
		String spreadsheetId = "1dgM6JG5GOc72B5a2ZdGlcIDYw9XPNV75cDB9h5e17vQ";
		List<Request> requests = new ArrayList<>();
		List<CellData> values = new ArrayList<>();
		values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(inscr.getEmail())));

		String sheetName = "";
		if (inscr.getTypeFormation().equals("PSC1")) {
			sheetName = "PSC1complet";
		} else {
			sheetName = "IPSENcomplet";
		}

		int row = 1;

		ValueRange response = null;
		try {
			response = service.spreadsheets().values().get(spreadsheetId, sheetName + "!A1:A60").execute();
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
			row = valuesCell.size();
		}

		requests.add(new Request().setUpdateCells(
				new UpdateCellsRequest().setStart(new GridCoordinate().setSheetId(0).setRowIndex(row).setColumnIndex(0))
						.setRows(Arrays.asList(new RowData().setValues(values))).setFields("userEnteredValue")));

		BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);

		service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
	}
}
