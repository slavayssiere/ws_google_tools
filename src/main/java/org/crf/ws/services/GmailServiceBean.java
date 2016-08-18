package org.crf.ws.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.crf.google.GoogleConnection;
import org.crf.models.Inscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;

@Service
public class GmailServiceBean implements GmailService {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	GoogleConnection gct = null;

    /* (non-Javadoc)
	 * @see org.crf.ws.services.GmailService#setToken(org.crf.google.GoogleConnection)
	 */
    @Override
	public void setToken(GoogleConnection newgct){
        gct = newgct;
    }  
    
    private Gmail getGmailService() throws Exception {
        Credential credential = gct.authorize();
        return new Gmail.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), credential)
                .setApplicationName(gct.getAPPLICATION_NAME())
                .build();
    }
    
    /* (non-Javadoc)
	 * @see org.crf.ws.services.GmailService#createDraft(java.lang.String)
	 */
    @Override
	public void createDraft(Inscription destinataire) throws Exception{
    	Gmail service = getGmailService();
    	
    	String content = "Bonjour, " + destinataire.getCivilite() + " " + destinataire.getPrenom() + " " + destinataire.getNom();    	
    	
    	MimeMessage message = createEmail(destinataire.getEmail(), "formez-vous.paris11@croix-rouge.fr", "Réponse à votre message d'inscription", content);
        
    	
    	Draft draft = new Draft();
        draft.setMessage(createMessageWithEmail(message));
        
        logger.info("Draft create, upload");
        
        draft = service.users().drafts().create("me", draft).execute();    	
    }
    
    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws AddressException 
     * @throws MessagingException
     */
    private static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText) throws AddressException, MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }
    
    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    private static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
    

}
