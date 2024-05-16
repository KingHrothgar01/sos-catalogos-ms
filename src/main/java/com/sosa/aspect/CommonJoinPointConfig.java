package com.sosa.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class CommonJoinPointConfig {

	@SuppressWarnings("squid:S1186") //suppress empty method warning
	@Pointcut("execution(public * com.sosa.controller.*.*(..))")
	public void controllersLayerExecution() {}

}
