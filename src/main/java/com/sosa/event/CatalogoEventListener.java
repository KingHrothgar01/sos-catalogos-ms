package com.sosa.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CatalogoEventListener implements ApplicationListener<CatalogoServiceEvent> {
	private final Logger LOGGER = LoggerFactory.getLogger(CatalogoEventListener.class);

	public void onApplicationEvent(CatalogoServiceEvent event) {
		CatalogoServiceEvent catalogoEvent = event;
		LOGGER.info("Catalogo {} with details : {}", event.getEventType(), catalogoEvent.getCatalogo());
	}
}