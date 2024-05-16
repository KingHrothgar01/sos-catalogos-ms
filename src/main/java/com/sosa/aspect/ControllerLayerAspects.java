package com.sosa.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Metrics;

@Aspect
@Configuration
public class ControllerLayerAspects {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerLayerAspects.class);

	@Before("execution (public * com.sosa.controllers.*Controller.*(..))")
	public void logBeforeRestCall(JoinPoint join) throws Throwable {
		LOGGER.info("+---- AOP Before REST call ----+" + join);
	}

	@AfterReturning("execution(public * com.sosa.controller.CatalogoMovimientoController.createMovimiento(..))")
	public void afterCallingCreateMovimiento(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning Create REST call ----+" + join);
		Metrics.counter("create_catalog_movement").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.CatalogoMovimientoController.getAllItems(..))")
	public void afterCallingGetAllMovimiento(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning get catalog_movements REST call ----+" + join);
		Metrics.counter("get_catalog_movements").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.CatalogoMovimientoController.getMovimiento(..))")
	public void afterCallingGetMovimiento(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning get catalog_movement REST call ----+" + join);
		Metrics.counter("get_catalog_movement").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.CatalogoMovimientoController.updateMovimiento(..))")
	public void afterCallingUpdateMovimiento(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning update catalog_movement REST call ----+" + join);
		Metrics.counter("update_catalog_movement").increment();
	}
	
	@AfterReturning("execution(public * com.sosa.controller.CatalogoMovimientoController.deleteMovimiento(..))")
	public void afterCallingDeleteMovimiento(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning delete catalog_movement REST call ----+" + join);
		Metrics.counter("delete_catalog_movement").increment();
	}

	@AfterThrowing(pointcut = "execution(public * com.sosa.controller.*.*(..))", throwing = "e")
	public void afterGetGreetingThrowsException(Exception e) {
		Metrics.counter("catalog_errors").increment();
	}
	
	@AfterReturning("execution(public * com.sosa.controller.CatalogoOperacionController.createOperacion(..))")
	public void afterCallingCreateCatalogOperation(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning Create REST call ----+" + join);
		Metrics.counter("create_catalog_operation").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.CatalogoOperacionController.getAllItems(..))")
	public void afterCallingGetAllCatalogOperation(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning get catalog_movements REST call ----+" + join);
		Metrics.counter("get_catalog_operations").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.CatalogoOperacionController.getOperacion(..))")
	public void afterCallingGetCatalogOperation(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning get catalog_movement REST call ----+" + join);
		Metrics.counter("get_catalog_operation").increment();
	}

	@AfterReturning("execution(public * com.sosa.controller.CatalogoOperacionController.updateOperacion(..))")
	public void afterCallingUpdateCatalogOperation(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning update catalog_movement REST call ----+" + join);
		Metrics.counter("update_catalog_operation").increment();
	}
	
	@AfterReturning("execution(public * com.sosa.controller.CatalogoOperacionController.deleteOperacion(..))")
	public void afterCallingDeleteCatalogOperation(JoinPoint join) {
		LOGGER.info("+---- AOP @AfterReturning delete catalog_movement REST call ----+" + join);
		Metrics.counter("delete_catalog_operation").increment();
	}
}
