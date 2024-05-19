package com.sosa.controller;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_008;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_010;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_012;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_013;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_014;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
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
import com.sosa.model.CatalogTransactionModelAssembler;
import com.sosa.model.dto.CatalogoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.service.CatalogoMovimientoService;

@RestController
public class CatalogoMovimientoController extends AbstractRestHandler {

	@Autowired
	private CatalogoMovimientoService catalogoMovimientoService;
	
	@Autowired
	private CatalogTransactionModelAssembler catalogTransactionModelAssembler;
	
	@Autowired
	private PagedResourcesAssembler<CatalogoDTO> pagedResourcesAssembler;

	@PostMapping(value = "/prestamos/v1/catalogos/movimientos", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public CatalogoDTO createMovimiento(@Valid @RequestBody CatalogoDTO dto, HttpServletRequest request, HttpServletResponse response) {
		dto = (this.catalogoMovimientoService.saveMovimiento(dto)).orElseThrow(
				() -> new HTTP500Exception(BUSINESS_MSG_ERR_CM_012));
		eventPublisher.publishEvent(new CatalogoServiceEvent(this, "ElementoCatalogoMovimientoCreado", dto));
		response.setHeader("Location", request.getRequestURL().append("/").append(dto.getIdCatalogo()).toString());
		return dto;
	}

	@GetMapping(value = "/prestamos/v1/catalogos/movimientos", produces =  "application/hal+json")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody PagedModel<CatalogoDTO> getAllItems(
			@RequestParam(value = "page", required = true, defaultValue = DEFAULT_PAGE_NUM) Integer page,
			@RequestParam(value = "size", required = true, defaultValue = DEFAULT_PAGE_SIZE) Integer size,
			@RequestParam(value = "property", required = true, defaultValue = DEFAULT_PAGE_PROPERTY) String prop,
			@RequestParam(value = "sortOrder", required = true, defaultValue = DEFAULT_PAGE_ORDER) String sortOrder,
			HttpServletRequest request, HttpServletResponse response) {
		Page<CatalogoDTO> catalogoPage = this.catalogoMovimientoService.findAllMovimientos(
				new PagingDTO(page, size, Direction.fromString(sortOrder.toUpperCase()), prop));
		return pagedResourcesAssembler.toModel(catalogoPage, catalogTransactionModelAssembler);
	}

	@GetMapping(value = "/prestamos/v1/catalogos/movimientos/{id}", produces = "application/hal+json")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Optional<CatalogoDTO> getMovimiento(@PathVariable("id") Long id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (id < 0)
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_010);
		Optional <CatalogoDTO> movimiento = this.catalogoMovimientoService.findMovimiento(id);
		return checkResourceFound(movimiento);
	}

	@PutMapping(value = "/prestamos/v1/catalogos/movimientos/{id}", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public CatalogoDTO updateMovimiento(@PathVariable("id") Long id, @Valid @RequestBody CatalogoDTO dto, HttpServletRequest request,
			HttpServletResponse response) {
		if (id.longValue() != dto.getIdCatalogo())
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_005);
		CatalogoDTO objetoActualizado = this.catalogoMovimientoService.updateMovimiento(dto).orElseThrow(
				() -> new HTTP500Exception(BUSINESS_MSG_ERR_CM_013));
		eventPublisher.publishEvent(new CatalogoServiceEvent(this, "ElementoCatalogoOperacionActualizado", objetoActualizado));
		return objetoActualizado;
	}

	@DeleteMapping(value = "/prestamos/v1/catalogos/movimientos/{id}", consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public CatalogoDTO deleteMovimiento(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		if (id < 0)
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_008);
		CatalogoDTO objetoEliminado = this.catalogoMovimientoService.deleteMovimiento(id).orElseThrow(
				() -> new HTTP500Exception(BUSINESS_MSG_ERR_CM_014));
        eventPublisher.publishEvent(new CatalogoServiceEvent(this, "ElementoCatalogoOperacionEliminado", objetoEliminado));
		return objetoEliminado;
	}
}
