package com.sosa.service;

import static com.sosa.util.Constants.APPLICATION_MESSAGE_002;
import static com.sosa.util.Constants.APPLICATION_PARAMETER_LARGE_PAGE;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_001;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_003;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_006;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_009;
import static com.sosa.util.Constants.CUSTOM_METRICS_LARGE_PAYLOAD;
import static com.sosa.util.Constants.REGISTRO_ACTIVO;
import static com.sosa.util.Constants.REGISTRO_INACTIVO;
import static com.sosa.util.Constants.USER_ALTA;

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
import com.sosa.model.CatalogoOperacion;
import com.sosa.model.dto.CatalogoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.repository.CatalogoOperacionRepository;

import io.micrometer.core.instrument.Metrics;

@Service
public class CatalogoOperacionService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CatalogoOperacionService.class);
	
	@Autowired
	private CatalogoOperacionRepository operacionRepository;

	public Optional<CatalogoDTO> saveOperacion(CatalogoDTO operacion) {
		LOGGER.info("Buscando registro en Cat\u00e1logo de Operaciones por campo descripción: " + operacion.getDescripcion());
		Optional<CatalogoOperacion> saved = operacionRepository.findByDescripcion(operacion.getDescripcion());
	
		if (saved.isPresent()) {
			LOGGER.error("No fue posible guardar el registro en el cat\u00e1logo de Operaciones, el registro ya existe: " + saved.get().getDescripcion());
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CO_001);
		}
		
        ModelMapper mapper = new ModelMapper();
		CatalogoOperacion model = mapper.map(operacion, CatalogoOperacion.class);
		model.setFechaRegistro(new Date());
		model.setUsuarioRegistra(USER_ALTA);
		model.setActivo(REGISTRO_ACTIVO);
		LOGGER.info("Guardando registro en Cat\u00e1logo de Operaciones, descripción: " + operacion.getDescripcion());
		return Optional.ofNullable(mapper.map(operacionRepository.save(model), CatalogoDTO.class));
	}

	public Optional<CatalogoDTO> findOperacion(long id) {
		LOGGER.info("Buscando registro en Cat\u00e1logo de Operaciones por campo id: " + id);
		Optional<CatalogoOperacion> model = operacionRepository.findById(id);
			
		if (model.isPresent()) {
			LOGGER.info("Se encont\u00F3 coincidencia en Cat\u00e1logo de Operaciones por campo id: {} - descripci\u00F3n: {}", id, model.get().getDescripcion());
			ModelMapper mapper = new ModelMapper();
            return Optional.ofNullable(mapper.map(model.get(), CatalogoDTO.class));
		}else {
			LOGGER.error("No se encontr\u00F3 registro en Cat\u00e1logo de Operaciones por campo id: " + id);
            throw new HTTP404Exception(BUSINESS_MSG_ERR_CO_003);
		}
	}

	public Optional<CatalogoDTO> updateOperacion(CatalogoDTO operacion) {		
		LOGGER.info("Buscando registro en Cat\u00e1logo de Operaciones por campo id: " + operacion.getIdCatalogo());
		Optional<CatalogoOperacion> saved = operacionRepository.findByDescripcion(operacion.getDescripcion());
			
		if (saved.isEmpty()) {
			LOGGER.error("No se encontr\u00F3 registro en Cat\u00e1logo de Operaciones por campo id: " + operacion.getIdCatalogo());
			throw new HTTP404Exception(BUSINESS_MSG_ERR_CO_006);
		}
			
		ModelMapper mapper = new ModelMapper();
		CatalogoOperacion model = mapper.map(operacion, CatalogoOperacion.class);
		model.setFechaRegistro(saved.get().getFechaRegistro());
		model.setUsuarioRegistra(saved.get().getUsuarioRegistra());
		model.setFechaActualizacion(new Date());
		model.setUsuarioActualiza(USER_ALTA);
		model.setActivo(saved.get().getActivo());
		
		LOGGER.info("Actualizando registro en Cat\u00e1logo de Operaciones, descripción: " + operacion.getDescripcion());
		return Optional.of(mapper.map(operacionRepository.save(model), CatalogoDTO.class));
	}

	public Optional<CatalogoDTO> deleteOperacion(long idOperacion) {
		LOGGER.info("Buscando registro en Cat\u00e1logo de Operaciones por campo id: " + idOperacion);
		Optional<CatalogoOperacion> saved = operacionRepository.findById(idOperacion);
			
		if (saved.isEmpty()) {
			LOGGER.error("No se encontr\u00F3 registro en Cat\u00e1logo de Operaciones por campo id: " + idOperacion);
				throw new HTTP404Exception(BUSINESS_MSG_ERR_CO_009);
		}
			
		ModelMapper mapper = new ModelMapper();
		saved.get().setActivo(REGISTRO_INACTIVO);
		saved.get().setFechaActualizacion(new Date());
		saved.get().setUsuarioActualiza("admin");
		CatalogoOperacion model = mapper.map(saved, CatalogoOperacion.class);
		LOGGER.info("Borrado l\u00F3gico de registro en Cat\u00e1logo de Operaciones por campo id: " + idOperacion);
		return Optional.of(mapper.map(operacionRepository.save(model), CatalogoDTO.class));
	}

	public Page<CatalogoDTO> findAllOperaciones(PagingDTO dto) {
		LOGGER.info("Buscando registros en Cat\u00e1logo de Operaciones.");
		Page<CatalogoOperacion> pageOfTransactions = null;
	
		if (dto.getOrder().isAscending())
			pageOfTransactions = operacionRepository.findAll(PageRequest.of(
					dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()));
		else
			pageOfTransactions = operacionRepository.findAll(PageRequest.of(
					dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).descending()));
		
		if (dto.getSize() > APPLICATION_PARAMETER_LARGE_PAGE) {
			LOGGER.warn(APPLICATION_MESSAGE_002);
			Metrics.counter(CUSTOM_METRICS_LARGE_PAYLOAD).increment();
		}
		return transform(pageOfTransactions);
	}
	
	private Page<CatalogoDTO> transform (Page<CatalogoOperacion> operacion) {
		return new PageImpl<CatalogoDTO>(
				getListOperacions(operacion), operacion.getPageable(), operacion.getTotalElements());
	}
	
	private List<CatalogoDTO> getListOperacions(Page<CatalogoOperacion> page){
		ModelMapper mapper = new ModelMapper();
		return page.getContent().stream().map(
				n -> mapper.map(n, CatalogoDTO.class)).collect(Collectors.toList());
	}
}
