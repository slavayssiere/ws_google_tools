package org.crf.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.internal.ProfileAssumeRoleCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
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
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
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
    
    private S3Object getS3Data(){
//    	AWSCredentials credentials = null;
//        try {
//            credentials = new ProfileCredentialsProvider().getCredentials();
//        } catch (Exception e) {
//            throw new AmazonClientException(
//                    "Cannot load the credentials from the credential profiles file. " +
//                    "Please make sure that your credentials file is at the correct " +
//                    "location (~/.aws/credentials), and is in valid format.",
//                    e);
//        }
        
        //AmazonS3 s3 = new AmazonS3Client(credentials);
    	
        AmazonS3 s3 = new AmazonS3Client();
        Region euWest1 = Region.getRegion(Regions.EU_WEST_1);
        s3.setRegion(euWest1);
        try
        {
        	return s3.getObject(new GetObjectRequest("static-private-file", "client_secret_oauth.json"));
        }
        catch(AmazonS3Exception ase){
    		throw ase;
    	}
    }
    
    private GoogleClientSecrets getClientSecret(){
    	
    	boolean s3test = false;
    	try
    	{
	    	this.clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
	                new InputStreamReader(getS3Data().getObjectContent()));
	    	s3test = true;
    	}
    	catch(AmazonS3Exception ase){
    		s3test = false;
            
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			s3test = false;
		}
    	
    	if(!s3test){
    		try {
				this.clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				        new InputStreamReader(GSheetService.class.getResourceAsStream("/client_secret_oauth.json")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
		return null;    	
    }

    public boolean token_create(String authCode, String redirectUri) throws Exception {
        
    	getClientSecret();
    	
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

    	getClientSecret();
    	
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
	
	/**
	 * Create a HttpRequestInitializer from the given one, except set the HTTP
	 * read timeout to be longer than the default (to allow called scripts time
	 * to execute).
	 *
	 * @param {HttpRequestInitializer}
	 *            requestInitializer the initializer to copy and adjust;
	 *            typically a Credential object.
	 * @return an initializer with an extended read timeout.
	 */
	public static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
		return new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest httpRequest) throws IOException {
				requestInitializer.initialize(httpRequest);
				// This allows the API to call (and avoid timing out on)
				// functions that take up to 6 minutes to complete (the maximum
				// allowed script run time), plus a little overhead.
				httpRequest.setReadTimeout(380000);
			}
		};
	}
}
