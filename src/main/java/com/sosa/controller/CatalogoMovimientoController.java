package com.sosa.controller;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_010;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_012;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_013;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_014;

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
import com.sosa.model.CatalogTransactionModelAssembler;
import com.sosa.model.dto.CatalogoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.service.CatalogoMovimientoService;

@RestController
@Validated
public class CatalogoMovimientoController extends AbstractRestHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CatalogoMovimientoController.class);
	private static final String DEFAULT_PAGE_PROPERTY = "idCatMovimiento";
	private static final String MOVEMENTS_PATH = "/prestamos/v1/catalogos/movimientos";
	private static final String MOVEMENT_BY_ID_PATH = MOVEMENTS_PATH + "/{id}";
	private static final String CREATE_MOVEMENT_EVENT = "ElementoCatalogoMovimientoCreado";
	private static final String UPDATE_MOVEMENT_EVENT = "ElementoCatalogoMovimientoActualizado";
	private static final String DELETE_MOVEMENT_EVENT = "ElementoCatalogoMovimientoEliminado";
	private static final String LOCATION_HEADER = "Location";

	@Autowired
	private CatalogoMovimientoService catalogoMovimientoService;

	@Autowired
	private CatalogTransactionModelAssembler catalogTransactionModelAssembler;

	@Autowired
	private PagedResourcesAssembler<CatalogoDTO> pagedResourcesAssembler;

	/**
	 * Creates a catalog movement entry.
	 *
	 * @param dto the movement data transfer object
	 * @param request the servlet request
	 * @param response the servlet response
	 * @return the created catalog movement
	 */
	@PostMapping(value = MOVEMENTS_PATH, consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public CatalogoDTO createMovimiento(@Valid @RequestBody CatalogoDTO dto, HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("Creando Entrada en Catálogo de Movimientos: {}", dto.getDescripcion());

		dto = catalogoMovimientoService.saveMovimiento(dto).orElseThrow(() -> {
			LOGGER.error("No fue posible guardar el registro en el catálogo de Movimientos.");
			return new HTTP500Exception(BUSINESS_MSG_ERR_CM_012);
		});
		eventPublisher.publishEvent(new CatalogoServiceEvent(this, CREATE_MOVEMENT_EVENT, dto));
		response.setHeader(LOCATION_HEADER, request.getRequestURL().append("/").append(dto.getIdCatalogo()).toString());
		return dto;
	}

	/**
	 * Retrieves all catalog movement records.
	 *
	 * @param page the page number
	 * @param size the page size
	 * @param prop the sort property
	 * @param sortOrder the sort order
	 * @param request the servlet request
	 * @param response the servlet response
	 * @return the paged model containing catalog movements
	 */
	@GetMapping(value = MOVEMENTS_PATH, produces = "application/hal+json")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody PagedModel<CatalogoDTO> getAllItems(
			@Valid @RequestParam(value = "page", required = true, defaultValue = DEFAULT_PAGE_NUM) @Min(value = 0, message = "El numero de página debe ser mayor o igual a cero.") Integer page,
			@Valid @RequestParam(value = "size", required = true, defaultValue = DEFAULT_PAGE_SIZE) @Min(value = 0, message = "El tamaño de página debe ser mayor a cero.") Integer size,
			@RequestParam(value = "property", required = true, defaultValue = DEFAULT_PAGE_PROPERTY) String prop,
			@RequestParam(value = "sortOrder", required = true, defaultValue = DEFAULT_PAGE_ORDER) String sortOrder,
			HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("Obteniendo registros de Catálogo de Movimientos");

		Page<CatalogoDTO> catalogoPage = catalogoMovimientoService.findAllMovimientos(
				new PagingDTO(page, size, Direction.fromString(sortOrder.toUpperCase()), prop));
		return pagedResourcesAssembler.toModel(catalogoPage, catalogTransactionModelAssembler);
	}

	/**
	 * Retrieves a catalog movement by identifier.
	 *
	 * @param id the movement identifier
	 * @param request the servlet request
	 * @param response the servlet response
	 * @return an optional catalog movement
	 * @throws Exception if an error occurs
	 */
	@GetMapping(value = MOVEMENT_BY_ID_PATH, produces = "application/hal+json")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Optional<CatalogoDTO> getMovimiento(@Valid @PathVariable("id") @Positive(message = "El identificador no puede ser negativo.") Long id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (id < 0) {
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_010);
		}

		LOGGER.info("Obteniendo registro de Catálogo de Movimientos, id: {}", id);

		Optional<CatalogoDTO> movimiento = catalogoMovimientoService.findMovimiento(id);
		return checkResourceFound(movimiento);
	}

	/**
	 * Updates a catalog movement entry.
	 *
	 * @param id the movement identifier
	 * @param dto the movement data transfer object
	 * @param request the servlet request
	 * @param response the servlet response
	 * @return the updated catalog movement
	 */
	@PutMapping(value = MOVEMENT_BY_ID_PATH, consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public CatalogoDTO updateMovimiento(@Valid @PathVariable("id") @Positive(message = "El identificador no puede ser negativo.") Long id, @Valid @RequestBody CatalogoDTO dto, HttpServletRequest request,
			HttpServletResponse response) {
		if (id.longValue() != dto.getIdCatalogo()) {
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_005);
		}

		LOGGER.info("Actualizando entrada en Catálogo de Movimientos, id: {}", id);

		CatalogoDTO objetoActualizado = catalogoMovimientoService.updateMovimiento(dto).orElseThrow(() -> {
			LOGGER.error("No fue posible actualizar el registro en el catálogo de Movimientos.");
			return new HTTP500Exception(BUSINESS_MSG_ERR_CM_013);
		});
		eventPublisher.publishEvent(new CatalogoServiceEvent(this, UPDATE_MOVEMENT_EVENT, objetoActualizado));
		return objetoActualizado;
	}

	/**
	 * Deletes a catalog movement entry.
	 *
	 * @param id the movement identifier
	 * @param request the servlet request
	 * @param response the servlet response
	 * @return the deleted catalog movement
	 */
	@DeleteMapping(value = MOVEMENT_BY_ID_PATH, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public CatalogoDTO deleteMovimiento(@Valid @PathVariable("id") @Positive(message = "El identificador no puede ser negativo.") Long id,
			HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("Eliminando entrada en Catálogo de Movimientos, id: {}", id);

		CatalogoDTO objetoEliminado = catalogoMovimientoService.deleteMovimiento(id).orElseThrow(() -> {
			LOGGER.error("No fue posible eliminar el registro en el catálogo de Movimientos.");
			return new HTTP500Exception(BUSINESS_MSG_ERR_CM_014);
		});
		eventPublisher.publishEvent(new CatalogoServiceEvent(this, DELETE_MOVEMENT_EVENT, objetoEliminado));
		return objetoEliminado;
	}
}