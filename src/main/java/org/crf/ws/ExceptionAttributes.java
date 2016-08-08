package org.crf.ws;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

public interface ExceptionAttributes {

	Map<String, Object> getExceptionsAttributes(Exception exeception, HttpServletRequest httprequest,
			HttpStatus httpStatus);
}
