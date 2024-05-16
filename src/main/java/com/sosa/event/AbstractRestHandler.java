package com.sosa.event;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP401Exception;
import com.sosa.exception.HTTP403Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.exception.HTTP500Exception;
import com.sosa.util.ErrorResponse;

import io.micrometer.core.instrument.Metrics;

import static com.sosa.util.Constants.HTTP_MSG_400;
import static com.sosa.util.Constants.HTTP_MSG_401;
import static com.sosa.util.Constants.HTTP_MSG_403;
import static com.sosa.util.Constants.HTTP_MSG_404;
import static com.sosa.util.Constants.HTTP_MSG_500;

public abstract class AbstractRestHandler implements ApplicationEventPublisherAware {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractRestHandler.class);
	
	protected ApplicationEventPublisher eventPublisher;
	protected static final String DEFAULT_PAGE_SIZE = "10";
	protected static final String DEFAULT_PAGE_NUM = "0";
	protected static final String DEFAULT_PAGE_PROPERTY = "idCatalogo";
	protected static final String DEFAULT_PAGE_ORDER = "ASC";

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HTTP400Exception.class)
	public @ResponseBody ErrorResponse handleDataStoreException(HTTP400Exception ex, WebRequest request,
			HttpServletResponse response) {
		LOGGER.info("Mensaje de Entrada con Errores: {}.", ex.getMessage());
		Metrics.counter("Error-en-la-Peticion").increment();
		return new ErrorResponse(ex, HTTP_MSG_400);
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(HTTP401Exception.class)
	public @ResponseBody ErrorResponse handleUnauthorizedException(HTTP401Exception ex, WebRequest request,
			HttpServletResponse response) {
		LOGGER.info("Error en la Autenticacion: {}.", ex.getMessage());
		Metrics.counter("Credenciales-Incorrectas").increment();
		return new ErrorResponse(ex, HTTP_MSG_401);
	}
	
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(HTTP403Exception.class)
	public @ResponseBody ErrorResponse handleForbiddenException(HTTP403Exception ex, WebRequest request,
			HttpServletResponse response) {
		LOGGER.info("Permisos Insuficientes: {}.", ex.getMessage());
		Metrics.counter("Permisos-Insuficietes").increment();
		return new ErrorResponse(ex, HTTP_MSG_403);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(HTTP404Exception.class)
	public @ResponseBody ErrorResponse handleResourceNotFoundException(HTTP404Exception ex, WebRequest request,
			HttpServletResponse response) {
		LOGGER.info("Recurso no Encontrado: {}.", ex.getMessage());
		Metrics.counter("Recurso-no-Encontrado").increment();
		return new ErrorResponse(ex, HTTP_MSG_404);
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(HTTP500Exception.class)
	public @ResponseBody ErrorResponse handleResourceNotFoundException(HTTP500Exception ex, WebRequest request,
			HttpServletResponse response) {
		LOGGER.info("Error en Servidor: {}.", ex.getMessage());
		Metrics.counter("Error-Servidor").increment();
		return new ErrorResponse(ex, HTTP_MSG_500);
	}

	//@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

	public static <T> Optional<T> checkResourceFound(final Optional<T> resource) {
		if (resource.isEmpty()) {
			throw new HTTP404Exception(HTTP_MSG_404);
		}
		return resource;
	}

}
