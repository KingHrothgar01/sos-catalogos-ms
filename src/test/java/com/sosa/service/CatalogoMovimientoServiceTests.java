package com.sosa.service;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_001;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_003;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_006;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_009;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.model.CatalogoMovimiento;
import com.sosa.model.dto.CatalogoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.repository.CatalogoMovimientoRepository;

@ExtendWith(MockitoExtension.class)
class CatalogoMovimientoServiceTests {

	@Mock
	CatalogoMovimientoRepository catalogoMovimientoRepository;
	
	@InjectMocks
	CatalogoMovimientoService catalogoMovimientoService;
	
	private CatalogoDTO dto;
	
	private Date fechaRegistro;
	
	private CatalogoMovimiento model;
	
	private PagingDTO paging;
	
	@BeforeEach
	private void setup() {
		fechaRegistro = new Date();
		dto = CatalogoDTO.builder()
				.idCatalogo(1)
				.descripcion("Abono")
				.fechaRegistro(fechaRegistro)
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		ModelMapper mapper = new ModelMapper();
		model = mapper.map(dto, CatalogoMovimiento.class);
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento.")
	void test_guardar_movimiento() {
		//given
		given(catalogoMovimientoRepository.findByDescripcion(dto.getDescripcion())).willReturn(Optional.empty());
		given(catalogoMovimientoRepository.save(any(CatalogoMovimiento.class))).willReturn(model);
		
		//when
		CatalogoDTO catalogoMovimientoGuardada = (catalogoMovimientoService.saveMovimiento(dto)).orElse(null);
		
		//then
		then(catalogoMovimientoGuardada).isNotNull();
		then(catalogoMovimientoGuardada.getDescripcion()).isEqualTo("Abono");
		then(catalogoMovimientoGuardada.getActivo()).isTrue();
	}
	
	@Test
	@DisplayName("Test para guardar un movimiento que ha sido guardado previamente - Escenario de error 1.")
	void test_guardar_movimiento_error_1() {
		//given
		given(catalogoMovimientoRepository.findByDescripcion(anyString())).willReturn(Optional.of(model));
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoMovimientoService.saveMovimiento(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CM_001));
		verify(catalogoMovimientoRepository, never()).save(any(CatalogoMovimiento.class));
	}
	
	@Test
	@DisplayName("Test para actualizar movimiento por id.")
	void test_actualizar_movimiento() {
		//given
		given(catalogoMovimientoRepository.findByDescripcion(anyString())).willReturn(Optional.of(model));
		given(catalogoMovimientoRepository.save(any(CatalogoMovimiento.class))).willReturn(model);
		
		//when
		Optional<CatalogoDTO> movimientoActualizado = catalogoMovimientoService.updateMovimiento(dto);
		
		//then
		assertThat(movimientoActualizado).isPresent();
	}
	
	@Test
	@DisplayName("Test para actualizar movimiento por id, registro no encontrado - Escenario de error 1.")
	void test_actualizar_movimiento_error_1() {
		//given
		given(catalogoMovimientoRepository.findByDescripcion(anyString())).willReturn(Optional.empty());
		
		//when
		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
			catalogoMovimientoService.updateMovimiento(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CM_006));
		verify(catalogoMovimientoRepository, never()).save(any(CatalogoMovimiento.class));
	}

	@Test
	@DisplayName("Test para listar todos los movimientos existentes en el catálogo en orden ascendente.")
	void test_listar_movimientos_ascendente() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idCatMovimiento");
		
		CatalogoMovimiento one = CatalogoMovimiento.builder()
				.idCatMovimiento(1)
				.descripcion("Disposicion")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		CatalogoMovimiento two = CatalogoMovimiento.builder()
				.idCatMovimiento(2)
				.descripcion("Interes")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<CatalogoMovimiento> pagina = new PageImpl<CatalogoMovimiento>(
				List.of(one, two),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 2);
		
		given(catalogoMovimientoRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()))).willReturn(pagina);
		
		//when
		Page<CatalogoDTO> movimientos = catalogoMovimientoService.findAllMovimientos(paging);
		
		//then
		assertThat(movimientos).isNotNull();
		assertThat(movimientos.getNumberOfElements()).isEqualTo(2);
		assertThat(movimientos.getTotalPages()).isEqualTo(1);
		assertThat(movimientos.getNumber()).isZero();
		assertThat(movimientos.getTotalElements()).isEqualTo(2);
		assertThat(movimientos.getSort()).isEqualTo(Sort.by("idCatMovimiento").ascending());
	}
	
	@Test
	@DisplayName("Test para listar todos los movimientos existentes en el catálogo en orden descendente.")
	void test_listar_movimientos_descendente() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.DESC);
		paging.setProperty("idCatMovimiento");
		
		CatalogoMovimiento one = CatalogoMovimiento.builder()
				.idCatMovimiento(1)
				.descripcion("Disposicion")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		CatalogoMovimiento two = CatalogoMovimiento.builder()
				.idCatMovimiento(2)
				.descripcion("Interes")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<CatalogoMovimiento> pagina = new PageImpl<CatalogoMovimiento>(
				List.of(one, two),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()), 2);
		
		given(catalogoMovimientoRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()))).willReturn(pagina);
		
		//when
		Page<CatalogoDTO> movimientos = catalogoMovimientoService.findAllMovimientos(paging);
		
		//then
		assertThat(movimientos).isNotNull();
		assertThat(movimientos.getNumberOfElements()).isEqualTo(2);
		assertThat(movimientos.getTotalPages()).isEqualTo(1);
		assertThat(movimientos.getNumber()).isZero();
		assertThat(movimientos.getTotalElements()).isEqualTo(2);
		assertThat(movimientos.getSort()).isEqualTo(Sort.by("idCatMovimiento").descending());
	}
	
	@Test
	@DisplayName("Test para listar todos los movimientos existentes en el catálogo, lista vac\u00EDa - Escenario de error 1.")
	void test_listar_movimientos_error_1() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idCatMovimiento");
		
		PageImpl<CatalogoMovimiento> pagina = new PageImpl<CatalogoMovimiento>(
				List.of(),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 0);
		
		given(catalogoMovimientoRepository.findAll(any(PageRequest.class))).willReturn(pagina);
		
		//when
		Page<CatalogoDTO> movimientos = catalogoMovimientoService.findAllMovimientos(paging);
		
		//then
		assertThat(movimientos).isNotNull();
		assertThat(movimientos).isEmpty();
		assertThat(movimientos.getNumberOfElements()).isZero();
		assertThat(movimientos.getTotalPages()).isZero();
		assertThat(movimientos.getNumber()).isZero();
		assertThat(movimientos.getTotalElements()).isZero();
		assertThat(movimientos.getSort()).isEqualTo(Sort.by("idCatMovimiento").ascending());
	}
	
	@Test
	@DisplayName("Test para listar todos los movimientos existentes de forma ascendente con numero de pagina mayor a 50.")
	void test_listar_movimientos_error_2() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(52);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idCatMovimiento");
		
		CatalogoMovimiento one = CatalogoMovimiento.builder()
				.idCatMovimiento(1)
				.descripcion("Disposicion")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		CatalogoMovimiento two = CatalogoMovimiento.builder()
				.idCatMovimiento(2)
				.descripcion("Interes")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<CatalogoMovimiento> pagina = new PageImpl<CatalogoMovimiento>(
				List.of(one, two),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 2);
		
		given(catalogoMovimientoRepository.findAll(any(PageRequest.class))).willReturn(pagina);
		
		//when
		Page<CatalogoDTO> clientes = catalogoMovimientoService.findAllMovimientos(paging);
		
		//then
		assertThat(clientes).isNotNull();
		assertThat(clientes.getNumberOfElements()).isEqualTo(2);
		assertThat(clientes.getTotalPages()).isEqualTo(1);
		assertThat(clientes.getNumber()).isZero();
		assertThat(clientes.getTotalElements()).isEqualTo(2);
		assertThat(clientes.getSort()).isEqualTo(Sort.by("idCatMovimiento").ascending());
	}
	
	@Test
	@DisplayName("Test para obtener catalogo de movimiento por id.")
	void test_obtener_catalogo_movimiento() {
		//given		
		given(catalogoMovimientoRepository.findById(anyLong())).willReturn(Optional.of(model));
		
		//when
		Optional<CatalogoDTO> movimiento = catalogoMovimientoService.findMovimiento(anyLong());
		
		//then
		assertThat(movimiento).isPresent();
	}
	
	@Test
	@DisplayName("Test para obtener catalogo de movimiento por id, Catalogo no encontrado - Escenario de error 2.")
	void test_obtener_tasa_interes_error_2() {
		//given
		given(catalogoMovimientoRepository.findById(anyLong())).willReturn(Optional.empty());
		
		//when
		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
			catalogoMovimientoService.findMovimiento(1);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CM_003));
	}

	@Test
	@DisplayName("Test para eliminar un movimiento por id.")
	void test_eliminar_movimiento() {
		//given
		given(catalogoMovimientoRepository.findById(anyLong())).willReturn(Optional.of(model));
		given(catalogoMovimientoRepository.save(any(CatalogoMovimiento.class))).willReturn(model);
		
		//when
		Optional<CatalogoDTO> catalogoMovimientoBorrada = catalogoMovimientoService.deleteMovimiento(1);
		
		//then
		then(catalogoMovimientoBorrada).isNotNull();
		then(catalogoMovimientoBorrada).isPresent();
		then(catalogoMovimientoBorrada.get().getActivo()).isFalse();
		verify(catalogoMovimientoRepository, times(1)).save(any(CatalogoMovimiento.class));
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento por id, catalogo no encontrado - Escenario de error 1.")
	void test_eliminar_movimiento_error_1() {
		//given
		given(catalogoMovimientoRepository.findById(anyLong())).willReturn(Optional.empty());
		
		//when
		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
			catalogoMovimientoService.deleteMovimiento(1);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CM_009));
		verify(catalogoMovimientoRepository, times(0)).save(any(CatalogoMovimiento.class));
	}
}
