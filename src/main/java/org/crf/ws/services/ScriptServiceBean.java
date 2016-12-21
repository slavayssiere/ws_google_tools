package org.crf.ws.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.crf.google.GoogleConnectionBean;
import org.crf.google.GoogleConnection;
import org.crf.models.ScriptData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.script.Script;
import com.google.api.services.script.model.ExecutionRequest;
import com.google.api.services.script.model.Operation;


@Service
public class ScriptServiceBean implements ScriptService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	GoogleConnection gct;
	SimpleDateFormat dt1 = new SimpleDateFormat("yyyy - MM - dd");

    /* (non-Javadoc)
	 * @see org.crf.google.ScriptService#setToken(org.crf.google.GConnectToken)
	 */
    @Override
	public void setToken(GoogleConnection newgct){
        gct = newgct;
    }
	private Script getScriptService() throws Exception {
		Credential credential = gct.authorize();
		return new Script.Builder(gct.getHTTP_TRANSPORT(), gct.getJSON_FACTORY(), GoogleConnectionBean.setHttpTimeout(credential))
				.setApplicationName(gct.getAPPLICATION_NAME()).build();
	}

	/* (non-Javadoc)
	 * @see org.crf.google.ScriptService#execute(org.crf.models.ScriptData)
	 */
	@Override
	public ScriptData execute(ScriptData sr) throws GoogleJsonResponseException {
		Script service;
		try {
			service = getScriptService();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return sr;
		}

		// Create an execution request object.
		ExecutionRequest request = new ExecutionRequest()
				.setFunction(sr.getFunctionName())
				.setParameters(new ArrayList<Object>(sr.getListArguments()))
				.setDevMode(true);
		Operation op = null;
		
		try{
			// Make the API request.
			op = service.scripts().run(sr.getScriptId(), request).execute();
		}catch(GoogleJsonResponseException gjre){
			throw gjre;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return sr;
		}

		// Print results of request.
		if (op.getError() != null) {
			// The API executed, but the script returned an error.
			System.out.println(getScriptError(op));
			sr.setError(getScriptError(op));
		} else {
			// The result provided by the API needs to be cast into
			// the correct type, based upon what types the Apps
			// Script function returns. Here, the function returns
			// an Apps Script Object with String keys and values,
			// so must be cast into a Java Map (folderSet).
			try {
				BigDecimal bd = (BigDecimal) op.getResponse().get("result");
				sr.setNbLines(bd.intValue());
			}
			catch(NullPointerException npe){
				logger.info("not a number");
			}
		}

		return sr;
	}

	/**
	 * Interpret an error response returned by the API and return a String
	 * summary.
	 *
	 * @param {Operation}
	 *            op the Operation returning an error response
	 * @return summary of error response, or null if Operation returned no error
	 */
	private String getScriptError(Operation op) {
		if (op.getError() == null) {
			return null;
		}

		// Extract the first (and only) set of error details and cast as a Map.
		// The values of this map are the script's 'errorMessage' and
		// 'errorType', and an array of stack trace elements (which also need to
		// be cast as Maps).
		Map<String, Object> detail = op.getError().getDetails().get(0);
		List<Map<String, Object>> stacktrace = (List<Map<String, Object>>) detail.get("scriptStackTraceElements");

		java.lang.StringBuilder sb = new StringBuilder("\nScript error message: ");
		sb.append(detail.get("errorMessage"));
		sb.append("\nScript error type: ");
		sb.append(detail.get("errorType"));

		if (stacktrace != null) {
			// There may not be a stacktrace if the script didn't start
			// executing.
			sb.append("\nScript error stacktrace:");
			for (Map<String, Object> elem : stacktrace) {
				sb.append("\n  ");
				sb.append(elem.get("function"));
				sb.append(":");
				sb.append(elem.get("lineNumber"));
			}
		}
		sb.append("\n");
		return sb.toString();
	}

}
