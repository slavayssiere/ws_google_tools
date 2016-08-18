package org.crf.ws.services;

import org.crf.google.GoogleConnection;
import org.crf.models.Inscription;

public interface GmailService {

	void setToken(GoogleConnection newgct);

	void createDraft(Inscription destinataire) throws Exception;

}