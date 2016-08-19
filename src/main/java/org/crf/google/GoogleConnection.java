package org.crf.google;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public interface GoogleConnection {

	boolean token_create(String authCode, String redirectUri) throws Exception;

	/**
	 * Creates an authorized Credential object.
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	Credential authorize() throws Exception;

	String getAccessToken();

	void setAccessToken(String accessToken);

	String getRefreshToken();

	void setRefreshToken(String refreshToken);

	Long getExpiresInSeconds();

	void setExpiresInSeconds(Long expiresInSeconds);

	HttpTransport getHTTP_TRANSPORT();

	void setHTTP_TRANSPORT(HttpTransport hTTP_TRANSPORT);

	String getAPPLICATION_NAME();

	JsonFactory getJSON_FACTORY();

	String getMessage();

	void setMessage(String message);

}