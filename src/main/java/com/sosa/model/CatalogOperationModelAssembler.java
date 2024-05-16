package com.sosa.model;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.sosa.controller.CatalogoOperacionController;
import com.sosa.model.dto.CatalogoDTO;

@Component
public class CatalogOperationModelAssembler extends RepresentationModelAssemblerSupport<CatalogoDTO, CatalogoDTO> {
	
	public CatalogOperationModelAssembler() {
		super(CatalogoOperacionController.class, CatalogoDTO.class);
	}

	@Override
	public CatalogoDTO toModel(CatalogoDTO entity) {
		ModelMapper mapper = new ModelMapper();
		return mapper.map(entity, CatalogoDTO.class);
	}

}
