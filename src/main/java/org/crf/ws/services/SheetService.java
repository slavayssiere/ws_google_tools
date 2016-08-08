package org.crf.ws.services;

import java.util.List;

import org.crf.google.GConnectToken;
import org.crf.models.Inscription;
import org.crf.models.Session;

public interface SheetService {

	void setToken(GConnectToken newgct);

	Session getState(String spreadsheetId, int id) throws Exception;

	void update(Session sess) throws Exception;

	List<Inscription> getDataFromGetEmail() throws Exception;

	boolean deleteRow(int numRow) throws Exception;

	void addInscription(String sheetid, int row, Inscription inscr) throws Exception;

	void addWaitingInscription(Inscription inscr) throws Exception;

	void addEmailNewDate(Inscription inscr) throws Exception;

}