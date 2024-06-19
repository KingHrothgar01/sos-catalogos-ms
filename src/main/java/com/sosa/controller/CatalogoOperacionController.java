package com.sosa.controller;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_014;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_010;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_012;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_013;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sosa.event.AbstractRestHandler;
import com.sosa.event.CatalogoServiceEvent;
import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP500Exception;
import com.sosa.model.CatalogOperationModelAssembler;
import com.sosa.model.dto.CatalogoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.service.CatalogoOperacionService;

@RestController
@Validated
public class CatalogoOperacionController extends AbstractRestHandler {

	@Autowired
	private CatalogoOperacionService catalogoOperacionService;
	
	@Autowired
	private CatalogOperationModelAssembler catalogOperationModelAssembler;
	
	@Autowired
	private PagedResourcesAssembler<CatalogoDTO> pagedResourcesAssembler;
	
	private static final String DEFAULT_PAGE_PROPERTY = "idCatOperacion";
	private static final Logger LOGGER = LoggerFactory.getLogger(CatalogoOperacionController.class);

	@PostMapping(value = "prestamos/v1/catalogos/operaciones", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public CatalogoDTO createOperacion(@Valid @RequestBody CatalogoDTO dto, HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("Creando Entrada en Cat\u00e1logo de Operaciones: " + dto.getDescripcion());
		
		dto = (this.catalogoOperacionService.saveOperacion(dto)).orElseThrow(
				() -> {
					LOGGER.error("No fue posible guardar el registro en el cat\u00e1logo de Operaciones.");
					return new HTTP500Exception(BUSINESS_MSG_ERR_CO_012);
				});

		eventPublisher.publishEvent(new CatalogoServiceEvent(this, "ElementoCatalogoOperacionCreado", dto));
		response.setHeader("Location", request.getRequestURL().append("/").append(dto.getIdCatalogo()).toString());
		return dto;
	}

	@GetMapping(value = "prestamos/v1/catalogos/operaciones", produces = "application/hal+json")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody PagedModel<CatalogoDTO> getAllItems(
			@Valid @RequestParam(value = "page", required = true, defaultValue = DEFAULT_PAGE_NUM) @Min(value = 0, message = "El numero de p\u00E1gina debe ser mayor o igual a cero.") Integer page,
			@Valid @RequestParam(value = "size", required = true, defaultValue = DEFAULT_PAGE_SIZE) @Min(value = 0, message = "El tama\u00F1o de p\u00E1gina debe ser mayor a cero.") Integer size,
			       @RequestParam(value = "property", required = true, defaultValue = DEFAULT_PAGE_PROPERTY) String prop,
			       @RequestParam(value = "sortOrder", required = true, defaultValue = DEFAULT_PAGE_ORDER) String sortOrder,
			HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("Obteniendo registros de Cat\u00e1logo de Operaciones");
		Page<CatalogoDTO> catalogoPage = this.catalogoOperacionService.findAllOperaciones(
				new PagingDTO(page, size, Direction.fromString(sortOrder.toUpperCase()), prop));
		return pagedResourcesAssembler.toModel(catalogoPage, catalogOperationModelAssembler);
	}

	@GetMapping(value = "prestamos/v1/catalogos/operaciones/{id}", produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Optional<CatalogoDTO> getOperacion(@Valid @PathVariable("id") @Positive(message = "El identificador no puede ser negativo.") Long id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (id < 0)
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CO_010);
		LOGGER.info("Obteniendo registro de Cat\u00e1logo de Operaciones, id: " + id);
		Optional <CatalogoDTO> operacion = this.catalogoOperacionService.findOperacion(id);
		return checkResourceFound(operacion);
	}

	@PutMapping(value = "prestamos/v1/catalogos/operaciones/{id}", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public CatalogoDTO updateOperacion(@PathVariable("id") @Positive(message = "El identificador no puede ser negativo.") Long id, 
			@Valid @RequestBody CatalogoDTO dto, HttpServletRequest request, HttpServletResponse response) {
		
		if (id.longValue() != dto.getIdCatalogo())
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CO_005);
		
		LOGGER.info("Actualizando entrada en Cat\u00e1logo de Operaciones, id: " + id);
		CatalogoDTO objetoActualizado = this.catalogoOperacionService.updateOperacion(dto).orElseThrow(
				() -> {
					LOGGER.error("No fue posible actualizar el registro en el cat\u00e1logo de Operaciones.");
					return new HTTP500Exception(BUSINESS_MSG_ERR_CO_013);
				});
		eventPublisher.publishEvent(new CatalogoServiceEvent(this, "ElementoCatalogoOperacionActualizado", objetoActualizado));
		return objetoActualizado;
	}

	@DeleteMapping(value = "prestamos/v1/catalogos/operaciones/{id}", produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public CatalogoDTO deleteOperacion(@Valid @PathVariable("id") @Positive(message = "El identificador no puede ser negativo.") Long id, 
	HttpServletRequest request, HttpServletResponse response) {
			LOGGER.info("Eliminando entrada en Cat\u00e1logo de Operaciones, id: " + id);
	    CatalogoDTO objetoEliminado = this.catalogoOperacionService.deleteOperacion(id).orElseThrow(
				() -> {
					LOGGER.error("No fue posible eliminar el registro en el cat\u00e1logo de Operaciones.");
					return new HTTP500Exception(BUSINESS_MSG_ERR_CM_014);
				});
		eventPublisher.publishEvent(new CatalogoServiceEvent(this, "ElementoCatalogoOperacionEliminado", objetoEliminado));
		return objetoEliminado;
	}

}
