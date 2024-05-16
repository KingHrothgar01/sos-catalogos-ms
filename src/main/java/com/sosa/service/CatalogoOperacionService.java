package com.sosa.service;

import static com.sosa.util.Constants.APPLICATION_MESSAGE_002;
import static com.sosa.util.Constants.APPLICATION_PARAMETER_LARGE_PAGE;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_001;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_002;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_003;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_004;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_006;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_007;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_008;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_009;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_010;
import static com.sosa.util.Constants.REGISTRO_ACTIVO;
import static com.sosa.util.Constants.REGISTRO_INACTIVO;

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
		if (operacion != null && operacion.getDescripcion() != null
				&& operacion.getDescripcion().length() > 0) {
			Optional<CatalogoOperacion> saved = operacionRepository.findByDescripcion(operacion.getDescripcion());
		
			if (saved.isPresent())
				throw new HTTP400Exception(BUSINESS_MSG_ERR_CO_001);
		
            ModelMapper mapper = new ModelMapper();
			CatalogoOperacion model = mapper.map(operacion, CatalogoOperacion.class);
			model.setFechaRegistro(new Date());
			model.setUsuarioRegistra("admin");
			model.setActivo(REGISTRO_ACTIVO);
			return Optional.ofNullable(mapper.map(operacionRepository.save(model), CatalogoDTO.class));
		}else
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CO_002);
	}

	public Optional<CatalogoDTO> findOperacion(long id) {
		if (id >= 0) {
			Optional<CatalogoOperacion> model = operacionRepository.findById(id);
			
		    if (model.isPresent()) {
				ModelMapper mapper = new ModelMapper();
                return Optional.ofNullable(mapper.map(model.get(), CatalogoDTO.class));
            } else
                throw new HTTP404Exception(BUSINESS_MSG_ERR_CO_003);
		}else
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CO_004);
	}

	public Optional<CatalogoDTO> updateOperacion(CatalogoDTO operacion) {
		if (operacion != null && operacion.getDescripcion() != null
				&& operacion.getDescripcion().length() > 0) {		
			Optional<CatalogoOperacion> saved = operacionRepository.findByDescripcion(operacion.getDescripcion());
			
			if (saved.isEmpty())
				throw new HTTP404Exception(BUSINESS_MSG_ERR_CO_006);
			
			if(!saved.get().getUsuarioRegistra().equalsIgnoreCase(operacion.getUsuarioRegistra()) ||
					!saved.get().getFechaRegistro().equals(operacion.getFechaRegistro()))
				throw new HTTP400Exception(BUSINESS_MSG_ERR_CO_007);
			
			ModelMapper mapper = new ModelMapper();
			CatalogoOperacion model = mapper.map(operacion, CatalogoOperacion.class);
			model.setFechaActualizacion(new Date());
			model.setUsuarioActualiza("admin");
			return Optional.of(mapper.map(operacionRepository.save(model), CatalogoDTO.class));
		}else
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CO_005);
	}

	public Optional<CatalogoDTO> deleteOperacion(long idOperacion) {
		if (idOperacion > 0) {
			Optional<CatalogoOperacion> saved = operacionRepository.findById(idOperacion);
			
			if (saved.isEmpty())
				throw new HTTP404Exception(BUSINESS_MSG_ERR_CO_009);
			
			ModelMapper mapper = new ModelMapper();
			saved.get().setActivo(REGISTRO_INACTIVO);
			saved.get().setFechaActualizacion(new Date());
			saved.get().setUsuarioActualiza("admin");
			CatalogoOperacion model = mapper.map(saved, CatalogoOperacion.class);
			return Optional.of(mapper.map(operacionRepository.save(model), CatalogoDTO.class));
		}else
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CO_008);
	}

	public Page<CatalogoDTO> findAllOperaciones(PagingDTO dto) {
        if (dto != null && dto.getOrder() != null && (dto.getPage() != null && dto.getPage() >= 0)
				&& (dto.getSize() != null && dto.getSize() > 0) && dto.getProperty() != null) {
			Page<CatalogoOperacion> pageOfTransactions = null;
		
			if (dto.getOrder().isAscending())
				pageOfTransactions = operacionRepository.findAll(PageRequest.of(
						dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()));
			else
				pageOfTransactions = operacionRepository.findAll(PageRequest.of(
						dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).descending()));
			
			//if (pageOfTransactions.getTotalElements() == 0)
			//	throw new HTTP404Exception(BUSINESS_MSG_ERR_CO_011);
			
			if (dto.getSize() > APPLICATION_PARAMETER_LARGE_PAGE) {
				LOGGER.info(APPLICATION_MESSAGE_002);
				Metrics.counter("large_payload").increment();
			}
			return transform(pageOfTransactions);
		}else
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CO_010);
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
