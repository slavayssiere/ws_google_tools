package org.crf.google;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

public class GConnectToken {
	 /** Application name. */
    private final String APPLICATION_NAME = "Google Sheets Formation";

    /** Directory to store user credentials for this application. */
    private final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/ws-google-crf.json");

    /** Global instance of the JSON factory. */
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private HttpTransport HTTP_TRANSPORT;
    
    private String accessToken;  
    private String refreshToken;  
    private Long expiresInSeconds;

    private static GoogleClientSecrets clientSecrets;
    
    /** Constructor
     * 
     */
    public GConnectToken() {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public boolean token_create(String authCode, String redirectUri) throws Exception {
        // load client secrets
    	AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        
        AmazonS3 s3 = new AmazonS3Client(credentials);
        Region euWest1 = Region.getRegion(Regions.EU_WEST_1);
        s3.setRegion(euWest1);
        
        S3Object object = s3.getObject(new GetObjectRequest("static-private-file", "client_secret_oauth.json"));
        
        //clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        //    new InputStreamReader(GSheetService.class.getResourceAsStream("/client_secret_oauth.json")));
        
        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
            new InputStreamReader(object.getObjectContent()));

        // set up authorization code flow
        GoogleTokenResponse tokenResponse =  
                new GoogleAuthorizationCodeTokenRequest(  
                    new NetHttpTransport(),  
                    JSON_FACTORY,  
                   "https://www.googleapis.com/oauth2/v4/token",  
                    clientSecrets.getDetails().getClientId(),  
                    clientSecrets.getDetails().getClientSecret(),  
                    authCode,  
                    redirectUri)  
                   .execute();  
        
        System.out.println("test");
        System.out.println("clientid:" + clientSecrets.getDetails().getClientId());
        System.out.println("clientsecret:" + clientSecrets.getDetails().getClientSecret());
        System.out.println("authCode:" + authCode);
        
        accessToken = tokenResponse.getAccessToken();  
        refreshToken = tokenResponse.getRefreshToken();  
        expiresInSeconds = tokenResponse.getExpiresInSeconds(); 
        
        System.out.println("accessToken:" + accessToken);
        System.out.println("refreshToken:" + refreshToken);
        System.out.println("expiresInSeconds:" + expiresInSeconds);
        
        
        return true;
      }
    
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize() throws Exception {
        // load client secrets
        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
            new InputStreamReader(GSheetService.class.getResourceAsStream("/client_secret_oauth.json")));
        
        // authorize        
        GoogleCredential credential = new GoogleCredential.Builder()  
                .setTransport(new NetHttpTransport())  
                .setJsonFactory(JSON_FACTORY)  
                .setClientSecrets(clientSecrets)  
                .build(); 
        
        credential.setAccessToken(accessToken);
        
        return credential;
      }

    @JsonProperty("token")
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Long getExpiresInSeconds() {
		return expiresInSeconds;
	}

	public void setExpiresInSeconds(Long expiresInSeconds) {
		this.expiresInSeconds = expiresInSeconds;
	}

	@JsonIgnore
	public HttpTransport getHTTP_TRANSPORT() {
		return HTTP_TRANSPORT;
	}

	public void setHTTP_TRANSPORT(HttpTransport hTTP_TRANSPORT) {
		HTTP_TRANSPORT = hTTP_TRANSPORT;
	}

	@JsonIgnore
	public String getAPPLICATION_NAME() {
		return APPLICATION_NAME;
	}

	@JsonIgnore
	public JsonFactory getJSON_FACTORY() {
		return JSON_FACTORY;
	}
}
