package com.sosa.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CatalogoServiceHealth implements HealthIndicator {
	
	@Autowired
	private ServiceProperties configuration;

	@Override
	public Health health() {
		return Health.up().withDetail("details",
				String.format("[ 'internals' : 'getting close to limit', 'profile' : '{}' ] {}", this.configuration.getName(), this.configuration.getDescription()))
				.status("itsok!").build();
	}
}