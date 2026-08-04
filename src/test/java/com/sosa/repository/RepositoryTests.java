package com.sosa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import com.sosa.model.CatalogoMovimiento;
import com.sosa.model.CatalogoOperacion;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@DisplayName("Repository Integration Tests")
class RepositoryTests {

	@Nested
	@DisplayName("CatalogoOperacionRepository Custom Methods Tests")
	class CatalogoOperacionRepositoryCustomTests {

		@Autowired
		private CatalogoOperacionRepository catalogoOperacionRepository;

		private CatalogoOperacion comision;
		private CatalogoOperacion disposicion;
		private CatalogoOperacion interes;

		@BeforeEach
		void setup() {
			comision = CatalogoOperacion.builder()
					.descripcion("Comision")
					.fechaRegistro(new Date())
					.usuarioRegistra("admin")
					.activo(true)
					.build();

			disposicion = CatalogoOperacion.builder()
					.descripcion("Disposicion")
					.fechaRegistro(new Date())
					.usuarioRegistra("admin")
					.activo(true)
					.build();

			interes = CatalogoOperacion.builder()
					.descripcion("Interes")
					.fechaRegistro(new Date())
					.usuarioRegistra("admin")
					.activo(true)
					.build();
		}

		@Test
		@DisplayName("Test findByDescripcion returns operation when exists")
		void test_find_by_descripcion_found() {
			catalogoOperacionRepository.save(comision);

			Optional<CatalogoOperacion> result = catalogoOperacionRepository
					.findByDescripcion("Comision");

			assertThat(result).isPresent();
			assertThat(result.get().getDescripcion()).isEqualTo("Comision");
		}

		@Test
		@DisplayName("Test findByDescripcion returns empty when not exists")
		void test_find_by_descripcion_not_found() {
			catalogoOperacionRepository.save(comision);

			Optional<CatalogoOperacion> result = catalogoOperacionRepository
					.findByDescripcion("NonExistent");

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Test findByDescripcion with multiple records returns first match")
		void test_find_by_descripcion_multiple_records() {
			catalogoOperacionRepository.save(comision);
			catalogoOperacionRepository.save(disposicion);
			catalogoOperacionRepository.save(interes);

			Optional<CatalogoOperacion> result = catalogoOperacionRepository
					.findByDescripcion("Disposicion");

			assertThat(result).isPresent();
			assertThat(result.get().getDescripcion()).isEqualTo("Disposicion");
		}

		@Test
		@DisplayName("Test findByDescripcion is case-sensitive")
		void test_find_by_descripcion_case_sensitive() {
			catalogoOperacionRepository.save(comision);

			Optional<CatalogoOperacion> result = catalogoOperacionRepository
					.findByDescripcion("comision");

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Test findByDescripcion with empty database")
		void test_find_by_descripcion_empty_database() {
			Optional<CatalogoOperacion> result = catalogoOperacionRepository
					.findByDescripcion("AnyDescription");

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Test findByDescripcion with inactive record")
		void test_find_by_descripcion_inactive_record() {
			comision.setActivo(false);
			catalogoOperacionRepository.save(comision);

			Optional<CatalogoOperacion> result = catalogoOperacionRepository
					.findByDescripcion("Comision");

			assertThat(result).isPresent();
			assertThat(result.get().getActivo()).isFalse();
		}

		@Test
		@DisplayName("Test repository can save operation")
		void test_save_operacion() {
			CatalogoOperacion saved = catalogoOperacionRepository.save(comision);

			assertThat(saved).isNotNull();
			assertThat(saved.getIdCatOperacion()).isPositive();
		}

		@Test
		@DisplayName("Test repository can delete operation")
		void test_delete_operacion() {
			CatalogoOperacion saved = catalogoOperacionRepository.save(comision);
			long id = saved.getIdCatOperacion();

			catalogoOperacionRepository.deleteById(id);
			Optional<CatalogoOperacion> result = catalogoOperacionRepository.findById(id);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Test repository findAll returns all records")
		void test_find_all_operaciones() {
			catalogoOperacionRepository.save(comision);
			catalogoOperacionRepository.save(disposicion);
			catalogoOperacionRepository.save(interes);

			Page<CatalogoOperacion> result = catalogoOperacionRepository
					.findAll(PageRequest.of(0, 10));

			assertThat(result.getTotalElements()).isEqualTo(3);
		}
	}

	@Nested
	@DisplayName("CatalogoMovimientoRepository Custom Methods Tests")
	class CatalogoMovimientoRepositoryCustomTests {

		@Autowired
		private CatalogoMovimientoRepository catalogoMovimientoRepository;

		private CatalogoMovimiento abono;
		private CatalogoMovimiento cargo;
		private CatalogoMovimiento transferencia;

		@BeforeEach
		void setup() {
			abono = CatalogoMovimiento.builder()
					.descripcion("Abono")
					.fechaRegistro(new Date())
					.usuarioRegistra("admin")
					.activo(true)
					.build();

			cargo = CatalogoMovimiento.builder()
					.descripcion("Cargo")
					.fechaRegistro(new Date())
					.usuarioRegistra("admin")
					.activo(true)
					.build();

			transferencia = CatalogoMovimiento.builder()
					.descripcion("Transferencia")
					.fechaRegistro(new Date())
					.usuarioRegistra("admin")
					.activo(true)
					.build();
		}

		@Test
		@DisplayName("Test findByDescripcion returns movement when exists")
		void test_find_by_descripcion_found() {
			catalogoMovimientoRepository.save(abono);

			Optional<CatalogoMovimiento> result = catalogoMovimientoRepository
					.findByDescripcion("Abono");

			assertThat(result).isPresent();
			assertThat(result.get().getDescripcion()).isEqualTo("Abono");
		}

		@Test
		@DisplayName("Test findByDescripcion returns empty when not exists")
		void test_find_by_descripcion_not_found() {
			catalogoMovimientoRepository.save(abono);

			Optional<CatalogoMovimiento> result = catalogoMovimientoRepository
					.findByDescripcion("NonExistent");

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Test findByDescripcion with multiple records returns first match")
		void test_find_by_descripcion_multiple_records() {
			catalogoMovimientoRepository.save(abono);
			catalogoMovimientoRepository.save(cargo);
			catalogoMovimientoRepository.save(transferencia);

			Optional<CatalogoMovimiento> result = catalogoMovimientoRepository
					.findByDescripcion("Cargo");

			assertThat(result).isPresent();
			assertThat(result.get().getDescripcion()).isEqualTo("Cargo");
		}

		@Test
		@DisplayName("Test findByDescripcion is case-sensitive")
		void test_find_by_descripcion_case_sensitive() {
			catalogoMovimientoRepository.save(abono);

			Optional<CatalogoMovimiento> result = catalogoMovimientoRepository
					.findByDescripcion("abono");

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Test findByDescripcion with empty database")
		void test_find_by_descripcion_empty_database() {
			Optional<CatalogoMovimiento> result = catalogoMovimientoRepository
					.findByDescripcion("AnyDescription");

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Test findByDescripcion with inactive record")
		void test_find_by_descripcion_inactive_record() {
			abono.setActivo(false);
			catalogoMovimientoRepository.save(abono);

			Optional<CatalogoMovimiento> result = catalogoMovimientoRepository
					.findByDescripcion("Abono");

			assertThat(result).isPresent();
			assertThat(result.get().getActivo()).isFalse();
		}

		@Test
		@DisplayName("Test repository can save movement")
		void test_save_movimiento() {
			CatalogoMovimiento saved = catalogoMovimientoRepository.save(abono);

			assertThat(saved).isNotNull();
			assertThat(saved.getIdCatMovimiento()).isPositive();
		}

		@Test
		@DisplayName("Test repository can delete movement")
		void test_delete_movimiento() {
			CatalogoMovimiento saved = catalogoMovimientoRepository.save(abono);
			long id = saved.getIdCatMovimiento();

			catalogoMovimientoRepository.deleteById(id);
			Optional<CatalogoMovimiento> result = catalogoMovimientoRepository.findById(id);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Test repository findAll returns all records")
		void test_find_all_movimientos() {
			catalogoMovimientoRepository.save(abono);
			catalogoMovimientoRepository.save(cargo);
			catalogoMovimientoRepository.save(transferencia);

			Page<CatalogoMovimiento> result = catalogoMovimientoRepository
					.findAll(PageRequest.of(0, 10));

			assertThat(result.getTotalElements()).isEqualTo(3);
		}

		@Test
		@DisplayName("Test repository paging with sorting")
		void test_repository_paging_with_sorting() {
			catalogoMovimientoRepository.save(abono);
			catalogoMovimientoRepository.save(cargo);
			catalogoMovimientoRepository.save(transferencia);

			Page<CatalogoMovimiento> result = catalogoMovimientoRepository
					.findAll(PageRequest.of(0, 2, Sort.by("descripcion").ascending()));

			assertThat(result.getSize()).isEqualTo(2);
			assertThat(result.getTotalPages()).isEqualTo(2);
			assertThat(result.getNumberOfElements()).isEqualTo(2);
		}
	}
}
