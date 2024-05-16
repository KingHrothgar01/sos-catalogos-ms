package com.sosa.service;

import static com.sosa.util.Constants.APPLICATION_MESSAGE_002;
import static com.sosa.util.Constants.APPLICATION_PARAMETER_LARGE_PAGE;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_001;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_002;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_003;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_004;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_006;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_007;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_008;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_009;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_010;
import static com.sosa.util.Constants.REGISTRO_INACTIVO;
import static com.sosa.util.Constants.REGISTRO_ACTIVO;

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
		if (movimiento != null && movimiento.getDescripcion() != null
				&& movimiento.getDescripcion().length() > 0) {
			Optional<CatalogoMovimiento> saved = movimientoRepository.findByDescripcion(movimiento.getDescripcion());
		
			if (saved.isPresent())
				throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_001);
		
		    ModelMapper mapper = new ModelMapper();
			CatalogoMovimiento model = mapper.map(movimiento, CatalogoMovimiento.class);
			model.setFechaRegistro(new Date());
			model.setUsuarioRegistra("admin");
			model.setActivo(REGISTRO_ACTIVO);
			return Optional.ofNullable(mapper.map(movimientoRepository.save(model), CatalogoDTO.class));
		}else
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_002);
	}

	public Optional<CatalogoDTO> findMovimiento(long id) {
		if (id >= 0) {
			Optional<CatalogoMovimiento> model = movimientoRepository.findById(id);
			
			if (model.isPresent()) {
				ModelMapper mapper = new ModelMapper();
				return Optional.ofNullable(mapper.map(model.get(), CatalogoDTO.class));
			}else
				throw new HTTP404Exception(BUSINESS_MSG_ERR_CM_003);
		}else
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_004);
	}

	public Optional<CatalogoDTO> updateMovimiento(CatalogoDTO movimiento) {
		if (movimiento != null && movimiento.getDescripcion() != null
				&& movimiento.getDescripcion().length() > 0) {
			Optional<CatalogoMovimiento> saved = movimientoRepository.findByDescripcion(movimiento.getDescripcion());
			
			if (saved.isEmpty())
				throw new HTTP404Exception(BUSINESS_MSG_ERR_CM_006);
			
			if(!saved.get().getUsuarioRegistra().equalsIgnoreCase(movimiento.getUsuarioRegistra()) ||
					!saved.get().getFechaRegistro().equals(movimiento.getFechaRegistro()))
				throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_007);
			
			ModelMapper mapper = new ModelMapper();
			CatalogoMovimiento model = mapper.map(movimiento, CatalogoMovimiento.class);
			model.setFechaActualizacion(new Date());
			model.setUsuarioActualiza("admin");
			return Optional.of(mapper.map(movimientoRepository.save(model), CatalogoDTO.class));
		}else
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_005);
	}

	public Optional<CatalogoDTO> deleteMovimiento(long idMovimiento) {
		if (idMovimiento > 0) {
			Optional<CatalogoMovimiento> saved = movimientoRepository.findById(idMovimiento);
			
			if (saved.isEmpty())
				throw new HTTP404Exception(BUSINESS_MSG_ERR_CM_009);
			
			ModelMapper mapper = new ModelMapper();
			saved.get().setActivo(REGISTRO_INACTIVO);
			saved.get().setFechaActualizacion(new Date());
			saved.get().setUsuarioActualiza("admin");
			CatalogoMovimiento model = mapper.map(saved, CatalogoMovimiento.class);
			return Optional.of(mapper.map(movimientoRepository.save(model), CatalogoDTO.class));
		}else
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_008);
	}

	public Page<CatalogoDTO> findAllMovimientos(PagingDTO dto) {
		if (dto != null && dto.getOrder() != null && (dto.getPage() != null && dto.getPage() >= 0)
				&& (dto.getSize() != null && dto.getSize() > 0) && dto.getProperty() != null) {
			Page<CatalogoMovimiento> pageOfTransactions = null;
			if (dto.getOrder().isAscending())
				pageOfTransactions = movimientoRepository.findAll(PageRequest.of(
						dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()));
			else
				pageOfTransactions = movimientoRepository.findAll(PageRequest.of(
						dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).descending()));
			
			//if (pageOfTransactions.getNumber() == 0)
			//	throw new HTTP404Exception(BUSINESS_MSG_ERR_CM_011);
			
			if (dto.getSize() > APPLICATION_PARAMETER_LARGE_PAGE) {
				LOGGER.info(APPLICATION_MESSAGE_002);
				Metrics.counter("large_payload").increment();
			}
			return transform(pageOfTransactions);
		}else
			throw new HTTP400Exception(BUSINESS_MSG_ERR_CM_010);
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
