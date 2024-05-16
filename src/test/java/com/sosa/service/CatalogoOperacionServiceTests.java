package com.sosa.service;

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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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
import com.sosa.model.CatalogoOperacion;
import com.sosa.model.dto.CatalogoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.repository.CatalogoOperacionRepository;

@ExtendWith(MockitoExtension.class)
class CatalogoOperacionServiceTests {

	@Mock
	CatalogoOperacionRepository catalogoOperacionRepository;
	
	@InjectMocks
	CatalogoOperacionService catalogoOperacionService;
	
	private CatalogoDTO dto;
	
	private Date fechaRegistro;
	
	private CatalogoOperacion model;
	
	private PagingDTO paging;
	
	@BeforeEach
	private void setup() {
		fechaRegistro = new Date();
		dto = CatalogoDTO.builder()
				.idCatalogo(1)
				.descripcion("Comision")
				.fechaRegistro(fechaRegistro)
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		ModelMapper mapper = new ModelMapper();
		model = mapper.map(dto, CatalogoOperacion.class);
	}
	
	@Test
	@DisplayName("Test para guardar una operación.")
	void test_guardar_operacion() {
		//given
		given(catalogoOperacionRepository.findByDescripcion(dto.getDescripcion())).willReturn(Optional.empty());
		given(catalogoOperacionRepository.save(any(CatalogoOperacion.class))).willReturn(model);
		
		//when
		CatalogoDTO catalogoOperacionGuardada = (catalogoOperacionService.saveOperacion(dto)).orElse(null);
		
		//then
		then(catalogoOperacionGuardada).isNotNull();
		then(catalogoOperacionGuardada.getDescripcion()).isEqualTo("Comision");
		then(catalogoOperacionGuardada.getActivo()).isTrue();
	}
	
	@Test
	@DisplayName("Test para guardar una operación, DTO nulo - Escenario de error 1.")
	void test_guardar_operacion_error_1() {
		//given
		//Catalogo Operación DTO es nulo
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.saveOperacion(null);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_002));
		verify(catalogoOperacionRepository, never()).findByDescripcion(anyString());
		verify(catalogoOperacionRepository, never()).save(any(CatalogoOperacion.class));
	}
	
	@Test
	@DisplayName("Test para guardar una operación, Descripción nula - Escenario de error 2.")
	void test_guardar_operacion_error_2() {
		//given
		dto.setDescripcion(null);
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.saveOperacion(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_002));
		verify(catalogoOperacionRepository, never()).findByDescripcion(anyString());
		verify(catalogoOperacionRepository, never()).save(any(CatalogoOperacion.class));
	}
	
	@Test
	@DisplayName("Test para guardar una operación, Descripción vacía - Escenario de error 3.")
	void test_guardar_operacion_error_3() {
		//given
		dto.setDescripcion("");
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.saveOperacion(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_002));
		verify(catalogoOperacionRepository, never()).findByDescripcion(anyString());
		verify(catalogoOperacionRepository, never()).save(any(CatalogoOperacion.class));
	}
	
	@Test
	@DisplayName("Test para guardar una operación que ha sido guardado previamente - Escenario de error 4.")
	void test_guardar_catalogo_operacion_error_4() {
		//given
		given(catalogoOperacionRepository.findByDescripcion(anyString())).willReturn(Optional.of(model));
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.saveOperacion(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_001));
		verify(catalogoOperacionRepository, never()).save(any(CatalogoOperacion.class));
	}
	
	@Test
	@DisplayName("Test para actualizar la operacion por id.")
	void test_actualizar_operacion() {
		//given
		given(catalogoOperacionRepository.findByDescripcion(anyString())).willReturn(Optional.of(model));
		given(catalogoOperacionRepository.save(any(CatalogoOperacion.class))).willReturn(model);
		
		//when
		Optional<CatalogoDTO> operacion = catalogoOperacionService.updateOperacion(dto);
		
		//then
		assertThat(operacion).isPresent();
	}
	
	@Test
	@DisplayName("Test para actualizar la operacion por id, DTO nulo - Escenario de error 1.")
	void test_actualizar_operacion_error_1() {
		//given
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.updateOperacion(null);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_005));
		verify(catalogoOperacionRepository, never()).findByDescripcion(anyString());
		verify(catalogoOperacionRepository, never()).save(any(CatalogoOperacion.class));
	}
	
	@Test
	@DisplayName("Test para actualizar la operacion por id, Descripción nula - Escenario de error 2.")
	void test_actualizar_operacion_error_2() {
		//given
		dto.setDescripcion(null);
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.updateOperacion(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_005));
		verify(catalogoOperacionRepository, never()).findByDescripcion(anyString());
		verify(catalogoOperacionRepository, never()).save(any(CatalogoOperacion.class));
	}
	
	@Test
	@DisplayName("Test para actualizar la operacion por id, Descripción vacía - Escenario de error 3.")
	void test_actualizar_operacion_error_3() {
		//given
		dto.setDescripcion("");
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.updateOperacion(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_005));
		verify(catalogoOperacionRepository, never()).findByDescripcion(anyString());
		verify(catalogoOperacionRepository, never()).save(any(CatalogoOperacion.class));
	}
	
	@Test
	@DisplayName("Test para actualizar la operacion por id, registro no encontrado - Escenario de error .")
	void test_actualizar_operacion_error_4() {
		//given
		given(catalogoOperacionRepository.findByDescripcion(anyString())).willReturn(Optional.empty());
		
		//when
		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
			catalogoOperacionService.updateOperacion(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_006));
		verify(catalogoOperacionRepository, never()).save(any(CatalogoOperacion.class));
	}
	
	@Test
	@DisplayName("Test para actualizar la operacion por id, intento de modificar datos de auditoría (usuarioRegistra) - Escenario de error 5.")
	void test_actualizar_operacion_error_5() {
		//given
		CatalogoOperacion saved = CatalogoOperacion.builder()
				.idCatOperacion(1)
				.descripcion("Comision")
				.fechaRegistro(fechaRegistro)
				.fechaActualizacion(null)
				.usuarioRegistra("impersonator")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		given(catalogoOperacionRepository.findByDescripcion(anyString())).willReturn(Optional.of(saved));
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.updateOperacion(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_007));
		verify(catalogoOperacionRepository, never()).save(any(CatalogoOperacion.class));
	}
	
	@Test
	@DisplayName("Test para actualizar la operacion por id, intento de modificar datos de auditoría (fechaRegistro) - Escenario de error 6.")
	void test_actualizar_operacion_error_6() {
		//given
		CatalogoOperacion saved = CatalogoOperacion.builder()
				.idCatOperacion(1)
				.descripcion("Comision")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		given(catalogoOperacionRepository.findByDescripcion(anyString())).willReturn(Optional.of(saved));
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.updateOperacion(dto);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_007));
		verify(catalogoOperacionRepository, never()).save(any(CatalogoOperacion.class));
	}
	
	
	
	@Test
	@DisplayName("Test para listar todas los operaciones existentes en el catálogo en orden ascendente.")
	void test_listar_operaciones_ascendente() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idCatOperacion");
		
		CatalogoOperacion one = CatalogoOperacion.builder()
				.idCatOperacion(1)
				.descripcion("Disposicion")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		CatalogoOperacion two = CatalogoOperacion.builder()
				.idCatOperacion(2)
				.descripcion("Interes")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<CatalogoOperacion> pagina = new PageImpl<CatalogoOperacion>(
				List.of(one, two),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 2);
		
		given(catalogoOperacionRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()))).willReturn(pagina);
		
		//when
		Page<CatalogoDTO> operaciones = catalogoOperacionService.findAllOperaciones(paging);
		
		//then
		assertThat(operaciones).isNotNull();
		assertThat(operaciones.getNumberOfElements()).isEqualTo(2);
		assertThat(operaciones.getTotalPages()).isEqualTo(1);
		assertThat(operaciones.getNumber()).isZero();
		assertThat(operaciones.getTotalElements()).isEqualTo(2);
		assertThat(operaciones.getSort()).isEqualTo(Sort.by("idCatOperacion").ascending());
	}
	
	@Test
	@DisplayName("Test para listar todas las operaciones existentes en el catálogo en orden descendente.")
	void test_listar_operaciones_descendente() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.DESC);
		paging.setProperty("idCatOperacion");
		
		CatalogoOperacion one = CatalogoOperacion.builder()
				.idCatOperacion(1)
				.descripcion("Disposicion")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		CatalogoOperacion two = CatalogoOperacion.builder()
				.idCatOperacion(2)
				.descripcion("Interes")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		PageImpl<CatalogoOperacion> pagina = new PageImpl<CatalogoOperacion>(
				List.of(one, two),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()), 2);
		
		given(catalogoOperacionRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).descending()))).willReturn(pagina);
		
		//when
		Page<CatalogoDTO> operaciones = catalogoOperacionService.findAllOperaciones(paging);
		
		//then
		assertThat(operaciones).isNotNull();
		assertThat(operaciones.getNumberOfElements()).isEqualTo(2);
		assertThat(operaciones.getTotalPages()).isEqualTo(1);
		assertThat(operaciones.getNumber()).isZero();
		assertThat(operaciones.getTotalElements()).isEqualTo(2);
		assertThat(operaciones.getSort()).isEqualTo(Sort.by("idCatOperacion").descending());
	}
	
	@Test
	@DisplayName("Test para listar todas las operaciones existentes en el catálogo, DTO nulo - Escenario de error 1.")
	void test_listar_operaciones_error_1() {
		//given
		//TasaInteres DTO es nulo
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.findAllOperaciones(null);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_010));
		verify(catalogoOperacionRepository, never()).findAll(any(PageRequest.class));
	}
	
	@Test
	@DisplayName("Test para listar todas las operaciones existentes en el catálogo, orden con valor incorrecto - Escenario de error 2.")
	void test_listar_operaciones_error_2() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setProperty("idCatOperacion");
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.findAllOperaciones(paging);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_010));
		verify(catalogoOperacionRepository, never()).findAll(any(PageRequest.class));
	}
	
	@Test
	@DisplayName("Test para listar todas las operaciones existentes en el catálogo, página con valor nulo - Escenario de error 3.")
	void test_listar_tasas_interes_error_3() {
		//given
		paging = new PagingDTO();
		paging.setOrder(Direction.ASC);
		paging.setSize(2);
		paging.setProperty("idCatOperacion");
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.findAllOperaciones(paging);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_010));
		verify(catalogoOperacionRepository, never()).findAll(any(PageRequest.class));
	}
	
	@Test
	@DisplayName("Test para listar todas las operaciones existentes en el catálogo, tamaño con valor nulo - Escenario de error 4.")
	void test_listar_tasas_interes_error_4() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idCatOperacion");
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.findAllOperaciones(paging);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_010));
		verify(catalogoOperacionRepository, never()).findAll(any(PageRequest.class));
	}
	
	@Test
	@DisplayName("Test para listar todas las operaciones existentes en el catálogo, columna con valor incorrecto - Escenario de error 5.")
	void test_listar_tasas_interes_error_5() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.ASC);
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.findAllOperaciones(paging);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_010));
		verify(catalogoOperacionRepository, never()).findAll(any(PageRequest.class));
	}
	
	@Test
	@DisplayName("Test para listar todas las operaciones existentes en el catálogo, página con valor incorrecto - Escenario de error 6.")
	void test_listar_tasas_interes_error_6() {
		//given
		paging = new PagingDTO();
		paging.setPage(-1);
		paging.setSize(2);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idCatOperacion");
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.findAllOperaciones(paging);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_010));
		verify(catalogoOperacionRepository, never()).findAll(any(PageRequest.class));
	}
	
	@Test
	@DisplayName("Test para listar todas las operaciones existentes en el catálogo, tamaño con valor incorrecto - Escenario de error 7.")
	void test_listar_tasas_interes_error_7() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(-1);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idCatOperacion");
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.findAllOperaciones(paging);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_010));
		verify(catalogoOperacionRepository, never()).findAll(any(PageRequest.class));
	}
	
	@Test
	@DisplayName("Test para listar todas las operaciones existentes en el catálogo, lista vacía - Escenario de error 8.")
	void test_listar_tasas_interes_error_8() {
		//given
		paging = new PagingDTO();
		paging.setPage(0);
		paging.setSize(2);
		paging.setOrder(Direction.ASC);
		paging.setProperty("idCatOperacion");
		
		PageImpl<CatalogoOperacion> pagina = new PageImpl<CatalogoOperacion>(
				List.of(),PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()), 0);
		
		given(catalogoOperacionRepository.findAll(PageRequest.of(paging.getPage(), paging.getSize(), Sort.by(paging.getProperty()).ascending()))).willReturn(pagina);
		
		//when
		Page<CatalogoDTO> operaciones = catalogoOperacionService.findAllOperaciones(paging);
		
		//then
		assertThat(operaciones).isNotNull();
		assertThat(operaciones).isEmpty();
		assertThat(operaciones.getNumberOfElements()).isZero();
		assertThat(operaciones.getTotalPages()).isZero();
		assertThat(operaciones.getNumber()).isZero();
		assertThat(operaciones.getTotalElements()).isZero();
		assertThat(operaciones.getSort()).isEqualTo(Sort.by("idCatOperacion").ascending());
	}
	
	@Test
	@DisplayName("Test para obtener catalogo de operacion por id.")
	void test_obtener_catalogo_operacion() {
		//given		
		given(catalogoOperacionRepository.findById(anyLong())).willReturn(Optional.of(model));
		
		//when
		Optional<CatalogoDTO> operacion = catalogoOperacionService.findOperacion(anyLong());
		
		//then
		assertThat(operacion).isPresent();
	}
	
	@Test
	@DisplayName("Test para obtener catalogo de operacion por id, id con valor incorrecto - Escenario de error 1.")
	void test_obtener_catalogo_operacion_error_1() {
		//given
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.findOperacion(-1);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_004));
		verify(catalogoOperacionRepository, never()).findById(anyLong());
	}
	
	@Test
	@DisplayName("Test para obtener catalogo de operacion por id, Catalogo no encontrado - Escenario de error 2.")
	void test_obtener_catalogo_operacion_error_2() {
		//given
		given(catalogoOperacionRepository.findById(anyLong())).willReturn(Optional.empty());
		
		//when
		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
			catalogoOperacionService.findOperacion(1);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_003));
	}

	@Test
	@DisplayName("Test para eliminar una operacion por id.")
	void test_eliminar_operacion() {
		//given
		given(catalogoOperacionRepository.findById(anyLong())).willReturn(Optional.of(model));
		given(catalogoOperacionRepository.save(any(CatalogoOperacion.class))).willReturn(model);
		
		//when
		Optional<CatalogoDTO> catalogoOperacionBorrada = catalogoOperacionService.deleteOperacion(1);
		
		//then
		then(catalogoOperacionBorrada).isNotNull();
		then(catalogoOperacionBorrada).isPresent();
		then(catalogoOperacionBorrada.get().getActivo()).isFalse();
	}
	
	@Test
	@DisplayName("Test para eliminar una operacion por id, id con valor incorrecto - Escenario de error 1.")
	void test_eliminar_operacion_error_1() {
		//given
		
		//when
		HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
			catalogoOperacionService.deleteOperacion(-1);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_008));
		verify(catalogoOperacionRepository, never()).findById(anyLong());
	}
	
	@Test
	@DisplayName("Test para eliminar una operacion por id, catalogo no encontrado - Escenario de error 2.")
	void test_eliminar_operacion_error_2() {
		//given
		given(catalogoOperacionRepository.findById(anyLong())).willReturn(Optional.empty());
		
		//when
		HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
			catalogoOperacionService.deleteOperacion(1);
		});
		
		//then
		assertTrue(thrown.getMessage().contains(BUSINESS_MSG_ERR_CO_009));
	}
}
