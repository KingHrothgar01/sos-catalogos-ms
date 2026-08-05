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
	private static final String AOP_PREFIX = "+---- AOP ";
	private static final String AOP_SUFFIX = " ----+";

	/**
	 * Logs all incoming REST calls before execution.
	 *
	 * @param join the join point containing information about the intercepted method
	 * @throws Throwable if an error occurs during execution
	 */
	@Before("execution (public * com.sosa.controllers.*Controller.*(..))")
	public void logBeforeRestCall(JoinPoint join) throws Throwable {
		LOGGER.info("{}Before REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
	}

	/**
	 * Logs and metrics tracking after creating a catalog movement.
	 *
	 * @param join the join point containing information about the intercepted method
	 */
	@AfterReturning("execution(public * com.sosa.controller.CatalogoMovimientoController.createMovimiento(..))")
	public void afterCallingCreateMovimiento(JoinPoint join) {
		LOGGER.info("{}@AfterReturning Create REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
		Metrics.counter("create_catalog_movement").increment();
	}

	/**
	 * Logs and metrics tracking after retrieving all catalog movements.
	 *
	 * @param join the join point containing information about the intercepted method
	 */
	@AfterReturning("execution(public * com.sosa.controller.CatalogoMovimientoController.getAllItems(..))")
	public void afterCallingGetAllMovimiento(JoinPoint join) {
		LOGGER.info("{}@AfterReturning get catalog_movements REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
		Metrics.counter("get_catalog_movements").increment();
	}

	/**
	 * Logs and metrics tracking after retrieving a specific catalog movement.
	 *
	 * @param join the join point containing information about the intercepted method
	 */
	@AfterReturning("execution(public * com.sosa.controller.CatalogoMovimientoController.getMovimiento(..))")
	public void afterCallingGetMovimiento(JoinPoint join) {
		LOGGER.info("{}@AfterReturning get catalog_movement REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
		Metrics.counter("get_catalog_movement").increment();
	}

	/**
	 * Logs and metrics tracking after updating a catalog movement.
	 *
	 * @param join the join point containing information about the intercepted method
	 */
	@AfterReturning("execution(public * com.sosa.controller.CatalogoMovimientoController.updateMovimiento(..))")
	public void afterCallingUpdateMovimiento(JoinPoint join) {
		LOGGER.info("{}@AfterReturning update catalog_movement REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
		Metrics.counter("update_catalog_movement").increment();
	}
	
	/**
	 * Logs and metrics tracking after deleting a catalog movement.
	 *
	 * @param join the join point containing information about the intercepted method
	 */
	@AfterReturning("execution(public * com.sosa.controller.CatalogoMovimientoController.deleteMovimiento(..))")
	public void afterCallingDeleteMovimiento(JoinPoint join) {
		LOGGER.info("{}@AfterReturning delete catalog_movement REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
		Metrics.counter("delete_catalog_movement").increment();
	}

	/**
	 * Tracks exceptions thrown during catalog controller operations.
	 *
	 * @param e the exception that was thrown
	 */
	@AfterThrowing(pointcut = "execution(public * com.sosa.controller.*.*(..))", throwing = "e")
	public void afterGetGreetingThrowsException(Exception e) {
		Metrics.counter("catalog_errors").increment();
	}
	
	/**
	 * Logs and metrics tracking after creating a catalog operation.
	 *
	 * @param join the join point containing information about the intercepted method
	 */
	@AfterReturning("execution(public * com.sosa.controller.CatalogoOperacionController.createOperacion(..))")
	public void afterCallingCreateCatalogOperation(JoinPoint join) {
		LOGGER.info("{}@AfterReturning Create REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
		Metrics.counter("create_catalog_operation").increment();
	}

	/**
	 * Logs and metrics tracking after retrieving all catalog operations.
	 *
	 * @param join the join point containing information about the intercepted method
	 */
	@AfterReturning("execution(public * com.sosa.controller.CatalogoOperacionController.getAllItems(..))")
	public void afterCallingGetAllCatalogOperation(JoinPoint join) {
		LOGGER.info("{}@AfterReturning get catalog_operations REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
		Metrics.counter("get_catalog_operations").increment();
	}

	/**
	 * Logs and metrics tracking after retrieving a specific catalog operation.
	 *
	 * @param join the join point containing information about the intercepted method
	 */
	@AfterReturning("execution(public * com.sosa.controller.CatalogoOperacionController.getOperacion(..))")
	public void afterCallingGetCatalogOperation(JoinPoint join) {
		LOGGER.info("{}@AfterReturning get catalog_operation REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
		Metrics.counter("get_catalog_operation").increment();
	}

	/**
	 * Logs and metrics tracking after updating a catalog operation.
	 *
	 * @param join the join point containing information about the intercepted method
	 */
	@AfterReturning("execution(public * com.sosa.controller.CatalogoOperacionController.updateOperacion(..))")
	public void afterCallingUpdateCatalogOperation(JoinPoint join) {
		LOGGER.info("{}@AfterReturning update catalog_operation REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
		Metrics.counter("update_catalog_operation").increment();
	}
	
	/**
	 * Logs and metrics tracking after deleting a catalog operation.
	 *
	 * @param join the join point containing information about the intercepted method
	 */
	@AfterReturning("execution(public * com.sosa.controller.CatalogoOperacionController.deleteOperacion(..))")
	public void afterCallingDeleteCatalogOperation(JoinPoint join) {
		LOGGER.info("{}@AfterReturning delete catalog_operation REST call{} {}", AOP_PREFIX, AOP_SUFFIX, join);
		Metrics.counter("delete_catalog_operation").increment();
	}
}
