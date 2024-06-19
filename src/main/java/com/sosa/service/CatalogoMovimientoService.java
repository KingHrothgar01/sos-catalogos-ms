package com.sosa.service;

import static com.sosa.util.Constants.APPLICATION_MESSAGE_002;
import static com.sosa.util.Constants.APPLICATION_PARAMETER_LARGE_PAGE;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_001;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_003;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_006;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_009;
import static com.sosa.util.Constants.REGISTRO_ACTIVO;
import static com.sosa.util.Constants.REGISTRO_INACTIVO;
import static com.sosa.util.Constants.USER_ALTA;
import static com.sosa.util.Constants.CUSTOM_METRICS_LARGE_PAYLOAD;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.model.CatalogoMovimiento;
import com.sosa.model.dto.CatalogoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.repository.CatalogoMovimientoRepository;

import io.micrometer.core.instrument.Metrics;

@Service
public class CatalogoMovimientoService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CatalogoMovimientoService.class);
	
	@Autowired
	private CatalogoMovimientoRepository movimientoRepository;

	public Optional<CatalogoDTO> saveMovimiento(CatalogoDTO movimiento) {
		LOGGER.info("Buscando registro en Cat\u00e1logo de Movimientos por campo descripción: " + movimiento.getDescripcion());
		Optional<CatalogoMovimiento> saved = movimientoRepository.findByDescripcion(movimiento.getDescripcion());
	
		if (saved.isPresent()) {
			LOGGER.error("No fue posible guardar el registro en el cat\u00e1logo de Movimientos, el registro ya existe: " + saved.get().getDescripcion());
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_001);
		}
	
	    ModelMapper mapper = new ModelMapper();
		CatalogoMovimiento model = mapper.map(movimiento, CatalogoMovimiento.class);
		model.setFechaRegistro(new Date());
		model.setUsuarioRegistra(USER_ALTA);
		model.setActivo(REGISTRO_ACTIVO);
		
		LOGGER.info("Guardando registro en Cat\u00e1logo de Movimientos, descripción: " + movimiento.getDescripcion());
		return Optional.ofNullable(mapper.map(movimientoRepository.save(model), CatalogoDTO.class));
	}

	public Optional<CatalogoDTO> findMovimiento(long id) {
		LOGGER.info("Buscando registro en Cat\u00e1logo de Movimientos por campo id: " + id);
		Optional<CatalogoMovimiento> model = movimientoRepository.findById(id);
			
		if (model.isPresent()) {
			LOGGER.info("Se encont\u00F3 coincidencia en Cat\u00e1logo de Movimientos por campo id: {} - descripci\u00F3n: {}", id, model.get().getDescripcion());
			ModelMapper mapper = new ModelMapper();
			return Optional.ofNullable(mapper.map(model.get(), CatalogoDTO.class));
		}else {
			LOGGER.error("No se encontr\u00F3 registro en Cat\u00e1logo de Movimientos por campo id: " + id);
			throw new HTTP404Exception(BUSINESS_MSG_ERR_CM_003);
		}
	}

	public Optional<CatalogoDTO> updateMovimiento(CatalogoDTO movimiento) {
		LOGGER.info("Buscando registro en Cat\u00e1logo de Movimientos por campo id: " + movimiento.getIdCatalogo());
		Optional<CatalogoMovimiento> saved = movimientoRepository.findByDescripcion(movimiento.getDescripcion());
		
		if (saved.isEmpty()) {
			LOGGER.error("No se encontr\u00F3 registro en Cat\u00e1logo de Movimientos por campo id: " + movimiento.getIdCatalogo());
			throw new HTTP404Exception(BUSINESS_MSG_ERR_CM_006);
		}
		
		ModelMapper mapper = new ModelMapper();
		CatalogoMovimiento model = mapper.map(movimiento, CatalogoMovimiento.class);
		model.setFechaRegistro(saved.get().getFechaRegistro());
		model.setUsuarioRegistra(saved.get().getUsuarioRegistra());
		model.setFechaActualizacion(new Date());
		model.setUsuarioActualiza(USER_ALTA);
		model.setActivo(saved.get().getActivo());
		
		LOGGER.info("Actualizando registro en Cat\u00e1logo de Movimientos, descripción: " + movimiento.getDescripcion());
		return Optional.of(mapper.map(movimientoRepository.save(model), CatalogoDTO.class));
	}

	public Optional<CatalogoDTO> deleteMovimiento(long idMovimiento) {
		LOGGER.info("Buscando registro en Cat\u00e1logo de Movimientos por campo id: " + idMovimiento);
		Optional<CatalogoMovimiento> saved = movimientoRepository.findById(idMovimiento);
		
		if (saved.isEmpty()) {
			LOGGER.error("No se encontr\u00F3 registro en Cat\u00e1logo de Movimientos por campo id: " + idMovimiento);
			throw new HTTP404Exception(BUSINESS_MSG_ERR_CM_009);
		}
		
		ModelMapper mapper = new ModelMapper();
		saved.get().setActivo(REGISTRO_INACTIVO);
		saved.get().setFechaActualizacion(new Date());
		saved.get().setUsuarioActualiza("admin");
		CatalogoMovimiento model = mapper.map(saved, CatalogoMovimiento.class);
		
		LOGGER.info("Borrado l\u00F3gico de registro en Cat\u00e1logo de Movimientos por campo id: " + idMovimiento);
		return Optional.of(mapper.map(movimientoRepository.save(model), CatalogoDTO.class));
	}

	public Page<CatalogoDTO> findAllMovimientos(PagingDTO dto) {
		LOGGER.info("Buscando registros en Cat\u00e1logo de Movimientos.");
		Page<CatalogoMovimiento> pageOfTransactions = null;
		
		if (dto.getOrder().isAscending())
			pageOfTransactions = movimientoRepository.findAll(PageRequest.of(
					dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()));
		else
			pageOfTransactions = movimientoRepository.findAll(PageRequest.of(
					dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).descending()));
		
		if (dto.getSize() > APPLICATION_PARAMETER_LARGE_PAGE) {
			LOGGER.warn(APPLICATION_MESSAGE_002);
			Metrics.counter(CUSTOM_METRICS_LARGE_PAYLOAD).increment();
		}
		return transform(pageOfTransactions);
	}
	
	private Page<CatalogoDTO> transform (Page<CatalogoMovimiento> movimiento) {
		return new PageImpl<CatalogoDTO>(
				getListMovimientos(movimiento), movimiento.getPageable(), movimiento.getTotalElements());
	}
	
	private List<CatalogoDTO> getListMovimientos(Page<CatalogoMovimiento> page){
		ModelMapper mapper = new ModelMapper();
		return page.getContent().stream().map(
				n -> mapper.map(n, CatalogoDTO.class)).collect(Collectors.toList());
	}
}
