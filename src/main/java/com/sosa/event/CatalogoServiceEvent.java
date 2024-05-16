package com.sosa.event;

import org.springframework.context.ApplicationEvent;

import com.sosa.model.dto.CatalogoDTO;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class CatalogoServiceEvent extends ApplicationEvent {

	private static final long serialVersionUID = -6121624308782503383L;
	
	private CatalogoDTO catalogo;
	private String eventType;

	public CatalogoServiceEvent(Object source, String eventType, CatalogoDTO eventCatalogo) {
		super(source);
		this.eventType = eventType;
		this.catalogo = eventCatalogo;
	}
	
	@Override
	public String toString() {
		return String.format("ClienteServiceEvent [evento = {}, eventType = {}]", catalogo.getIdCatalogo(), eventType);
	}
}
