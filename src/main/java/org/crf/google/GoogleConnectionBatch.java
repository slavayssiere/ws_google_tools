package org.crf.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.crf.ws.services.SheetServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

public class GoogleConnectionBatch implements GoogleConnection {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/** Application name. */
	private final String APPLICATION_NAME = "Google Sheets Formation";

	/** Directory to store user credentials for this application. */
	private final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".credentials/ws-google-crf.json");

	/** Global instance of the JSON factory. */
	private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private HttpTransport HTTP_TRANSPORT;

	private String accessToken;
	private String refreshToken;
	private Long expiresInSeconds;

	private String message;

	private static GoogleClientSecrets clientSecrets;

	private GoogleCredential credential = null;

	private List<String> scopes;

	/** Constructor
    * 
    */
   public GoogleConnectionBatch() {
       try {
           HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
           new FileDataStoreFactory(DATA_STORE_DIR);
       } catch (Throwable t) {
           t.printStackTrace();
           System.exit(1);
       }
       
       scopes = new ArrayList<String>();

       scopes.add("profile");
       scopes.add("email");
       scopes.add("https://mail.google.com/");
       scopes.add("https://www.googleapis.com/auth/gmail.compose");
       scopes.add("https://www.googleapis.com/auth/spreadsheets");
       scopes.add("https://www.googleapis.com/auth/drive");
       scopes.add("https://www.googleapis.com/auth/documents");
       scopes.add("https://www.googleapis.com/auth/drive.scripts");
       scopes.add("https://www.googleapis.com/auth/userinfo.email");
       scopes.add("https://www.googleapis.com/auth/calendar");
       scopes.add("https://www.googleapis.com/auth/script.external_request");
       
   }

	private S3Object getS3Data(String filename) {
		AmazonS3 s3 = new AmazonS3Client();
		Region euWest1 = Region.getRegion(Regions.EU_WEST_1);
		s3.setRegion(euWest1);
		try {
			return s3.getObject(new GetObjectRequest("static-private-file", filename));
		} catch (AmazonS3Exception ase) {
			throw ase;
		}
	}

	private GoogleClientSecrets getClientSecret() {

		boolean s3test = false;
		try {
			this.clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
					new InputStreamReader(getS3Data("formation-management.json").getObjectContent()));
			s3test = true;
		} catch (AmazonS3Exception ase) {
			System.out.println("we are on dev machine");
			s3test = false;

		} catch (IOException e) {
			e.printStackTrace();
			s3test = false;
		}

		if (!s3test) {
			try {
				this.clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(
						SheetServiceBean.class.getResourceAsStream("/formation-management.json")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	private InputStream getSecretFile() {
		try {
			return (InputStream) getS3Data("formation-management.json").getObjectContent();
		} catch (AmazonS3Exception ase) {
			logger.info("we are on dev machine");
		}

		return SheetServiceBean.class.getResourceAsStream("/formation-management.json");
	}

	@Override
	public boolean token_create(String authCode, String redirectUri) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Credential authorize() throws Exception {

		if (this.credential == null) {
			this.credential = GoogleCredential.fromStream(getSecretFile()).createScoped(scopes);
		}

		return credential;
	}

	@Override
	public String getAccessToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAccessToken(String accessToken) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getRefreshToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRefreshToken(String refreshToken) {
		// TODO Auto-generated method stub

	}

	@Override
	public Long getExpiresInSeconds() {
		return expiresInSeconds;
	}

	@Override
	public void setExpiresInSeconds(Long expiresInSeconds) {
		this.expiresInSeconds = expiresInSeconds;

	}

	@Override
	public HttpTransport getHTTP_TRANSPORT() {
		return this.HTTP_TRANSPORT;
	}

	@Override
	public void setHTTP_TRANSPORT(HttpTransport hTTP_TRANSPORT) {
		this.HTTP_TRANSPORT = hTTP_TRANSPORT;
	}

	@Override
	public String getAPPLICATION_NAME() {
		return APPLICATION_NAME;
	}

	@Override
	public JsonFactory getJSON_FACTORY() {
		return JSON_FACTORY;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

}
