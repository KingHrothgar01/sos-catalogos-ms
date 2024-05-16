package com.sosa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

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
	CatalogoOperacionRepository catalogo;
	
	private CatalogoOperacion catOperacion;
	
	@BeforeEach
	private void setup() {
		catOperacion = CatalogoOperacion.builder()
				.descripcion("Comision")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.build();
	}

	@Test
	@DisplayName("Test para guardar una operacion.")
	void testGuardarOperacion() {
		//given
		CatalogoOperacion operacion = CatalogoOperacion.builder()
				.descripcion("Disposicion")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.build();
		
		//when
		CatalogoOperacion operacionGuardada = catalogo.save(operacion);
		
		//then
		assertThat(operacionGuardada).isNotNull();
		assertThat(operacionGuardada.getIdCatOperacion()).isGreaterThan(0);
		
	}
	
	@Test
	@DisplayName("Test para listar las operaciones.")
	void testListarOperacions() {
		//given
		CatalogoOperacion operacion = CatalogoOperacion.builder()
				.descripcion("Interes")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.build();
		catalogo.save(operacion);
		catalogo.save(catOperacion);
		//when
		Page<CatalogoOperacion> listaOperacions = catalogo.findAll(PageRequest.of(1, 2, Sort.by("descripcion").ascending()));
		//then
		assertThat(listaOperacions).isNotNull();
		assertThat(listaOperacions.getSize()).isEqualTo(2);
	}

	@Test
	@DisplayName("Test para obtener una operacion.")
	void testObtenerOperacionPorId() {
		//given
		catalogo.save(catOperacion);
		//when
		CatalogoOperacion operacion = catalogo.findById(catOperacion.getIdCatOperacion()).get();
		//then
		assertThat(operacion).isNotNull();
	}
	
	@Test
	@DisplayName("Test para actualizar una operacion.")
	void testActualizarOperacion() {
		//given
		catalogo.save(catOperacion);
		//when
		CatalogoOperacion operacion = catalogo.findById(catOperacion.getIdCatOperacion()).get();
		operacion.setDescripcion("Impuesto");
		CatalogoOperacion operacionActualizado = catalogo.save(operacion);
		//then
		assertThat(operacionActualizado.getDescripcion()).isEqualTo("Impuesto");
	}
	
	@Test
	@DisplayName("Test para eliminar una operacion.")
	void testEliminarOperacion() {
		//given
		catalogo.save(catOperacion);
		//when
		catalogo.deleteById(catOperacion.getIdCatOperacion());
		Optional<CatalogoOperacion> operacion = catalogo.findById(catOperacion.getIdCatOperacion());
		//then
		assertThat(operacion).isEmpty();
	}
}
