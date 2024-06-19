package com.sosa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sosa.model.CatalogoOperacion;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
class CatalogoOperacionRepositoryTests {
	
	@Autowired
	CatalogoOperacionRepository catalogoOperacionRepository;
	
	private CatalogoOperacion comision;
	
	@BeforeEach
	private void setup() {
		comision = CatalogoOperacion.builder()
				.descripcion("Comision")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
	}

	@Test
	@DisplayName("Test para guardar una operacion.")
	void test_guardar_operacion() {
		//given
		//comision
		
		//when
		CatalogoOperacion operacionGuardada = catalogoOperacionRepository.save(comision);
		
		//then
		assertThat(operacionGuardada).isNotNull();
		assertThat(operacionGuardada.getIdCatOperacion()).isPositive();
		
	}
	
	@Test
	@DisplayName("Test para actualizar una operacion.")
	void test_actualizar_operacion() {
		Date fechaActualizacion = new Date();
		//given
		catalogoOperacionRepository.save(comision);
		
		//when
		CatalogoOperacion operacionGuardado = catalogoOperacionRepository.findById(comision.getIdCatOperacion()).orElse(null);
		operacionGuardado.setFechaActualizacion(fechaActualizacion);
		operacionGuardado.setDescripcion("Impuesto");
		operacionGuardado.setUsuarioActualiza("admin");
		CatalogoOperacion operacionActualizado = catalogoOperacionRepository.save(operacionGuardado);
		
		//then
		assertThat(operacionActualizado).isNotNull();
		assertThat(operacionActualizado.getDescripcion()).isEqualTo("Impuesto");
		assertThat(operacionActualizado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
		assertThat(operacionActualizado.getUsuarioActualiza()).isEqualTo("admin");
	}
	
	@Test
	@DisplayName("Test para eliminar una operacion.")
	void test_eliminar_operacion() {
		Date fechaActualizacion = new Date();
		//given
		catalogoOperacionRepository.save(comision);
		
		//when
		CatalogoOperacion operacionGuardada = catalogoOperacionRepository.findById(comision.getIdCatOperacion()).orElse(null);
		operacionGuardada.setActivo(false);
		operacionGuardada.setFechaActualizacion(fechaActualizacion);
		operacionGuardada.setUsuarioActualiza("admin");
		CatalogoOperacion operacionActualizada = catalogoOperacionRepository.save(operacionGuardada);
		
		//then
		assertThat(operacionActualizada).isNotNull();
		assertThat(operacionActualizada.getActivo()).isFalse();
		assertThat(operacionActualizada.getFechaActualizacion()).isEqualTo(fechaActualizacion);
		assertThat(operacionActualizada.getUsuarioActualiza()).isEqualTo("admin");
	}
	
	@Test
	@DisplayName("Test para listar las operaciones.")
	void test_listar_operaciones() {
		//given
		CatalogoOperacion operacion = CatalogoOperacion.builder()
				.descripcion("Interes")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.build();
		
		catalogoOperacionRepository.save(operacion);
		catalogoOperacionRepository.save(comision);
		
		//when
		Page<CatalogoOperacion> listaOperaciones = catalogoOperacionRepository.findAll(PageRequest.of(0, 2, Sort.by("descripcion").ascending()));
		
		//then
		assertThat(listaOperaciones).isNotNull();
		assertThat(listaOperaciones.getSize()).isEqualTo(2);
		assertThat(listaOperaciones.getNumber()).isZero();
		assertThat(listaOperaciones.getNumberOfElements()).isEqualTo(2);
		assertThat(listaOperaciones.getSort().toString()).hasToString("descripcion: ASC");
		assertThat(listaOperaciones.getTotalElements()).isEqualTo(2);
		assertThat(listaOperaciones.getTotalPages()).isEqualTo(1);
		assertThat(listaOperaciones.getContent().get(0).getDescripcion()).isEqualTo("Comision");
	}

	@Test
	@DisplayName("Test para obtener una operacion.")
	void test_obtener_operacion_por_id() {
		//given
		catalogoOperacionRepository.save(comision);
		
		//when
		CatalogoOperacion operacionGuardada = catalogoOperacionRepository.findById(comision.getIdCatOperacion()).orElse(null);
		
		//then
		assertThat(operacionGuardada).isNotNull();
		assertThat(operacionGuardada.getIdCatOperacion()).isNotNegative();
		assertThat(operacionGuardada.getActivo()).isTrue();
	}
}
