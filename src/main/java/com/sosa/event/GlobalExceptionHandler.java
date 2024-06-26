package com.sosa.event;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.sosa.util.ErrorResponse;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_002;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_004;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_008;
import static com.sosa.util.Constants.HTTP_MSG_400;
import static com.sosa.util.Constants.HTTP_MSG_500;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	 
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public @ResponseBody ErrorResponse handleException(RuntimeException ex, WebRequest request,
			HttpServletResponse response) {
		
		LOGGER.info("Error en Servidor.: {}.", ex.getMessage());
		return new ErrorResponse(ex, HTTP_MSG_500);
    }

	@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public @ResponseBody ErrorResponse handleNotReadableException(RuntimeException ex, WebRequest request,
			HttpServletResponse response) {
		
		LOGGER.info("Error en Petición Cliente.: {}.", ex.getMessage());
		return getOutputMessage((ServletWebRequest)request, ex);
    }
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public @ResponseBody ErrorResponse handleArgumentNotValidException(BindException ex, WebRequest request,
			HttpServletResponse response) {
		
		LOGGER.info("Error en Petición Cliente.: {}.", ex.getMessage());
		return getOutputMessage((ServletWebRequest)request, ex);
    }
	
	private ErrorResponse getOutputMessage(ServletWebRequest request, Exception ex) {
		if (request.getHttpMethod().equals(HttpMethod.POST))
			return new ErrorResponse(BUSINESS_MSG_ERR_CO_002, HTTP_MSG_400);
		else if (request.getHttpMethod().equals(HttpMethod.PUT))
			return new ErrorResponse(BUSINESS_MSG_ERR_CO_005, HTTP_MSG_400);
		else if (request.getHttpMethod().equals(HttpMethod.GET))
			return new ErrorResponse(BUSINESS_MSG_ERR_CO_004, HTTP_MSG_400);
		else if (request.getHttpMethod().equals(HttpMethod.GET))
			return new ErrorResponse(BUSINESS_MSG_ERR_CO_008, HTTP_MSG_400);
		else
			return new ErrorResponse(ex, HTTP_MSG_400);
			
	}
}