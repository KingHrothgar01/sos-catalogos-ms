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

import com.sosa.model.CatalogoMovimiento;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
class CatalogoMovimientoRepositoryTests {
	
	@Autowired
	CatalogoMovimientoRepository catalogoMovimientoRepository;
	
	private CatalogoMovimiento abono;
	
	@BeforeEach
	private void setup() {
		abono = CatalogoMovimiento.builder()
				.descripcion("Abono")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
	}

	@Test
	@DisplayName("Test para guardar un movimiento.")
	void test_guardar_movimiento() {
		//given
		//abono
		
		//when
		CatalogoMovimiento movimientoGuardado = catalogoMovimientoRepository.save(abono);
		
		//then
		assertThat(movimientoGuardado).isNotNull();
		assertThat(movimientoGuardado.getIdCatMovimiento()).isPositive();
	}
	
	@Test
	@DisplayName("Test para actualizar un movimiento.")
	void test_actualizar_movimiento() {
		Date fechaActualizacion = new Date();
		//given
		catalogoMovimientoRepository.save(abono);
		
		//when
		CatalogoMovimiento movimientoGuardado = catalogoMovimientoRepository.findById(abono.getIdCatMovimiento()).orElse(null);
		movimientoGuardado.setFechaActualizacion(fechaActualizacion);
		movimientoGuardado.setDescripcion("Credit");
		movimientoGuardado.setUsuarioActualiza("admin");
		CatalogoMovimiento movimientoActualizado = catalogoMovimientoRepository.save(movimientoGuardado);
		
		//then
		assertThat(movimientoActualizado).isNotNull();
		assertThat(movimientoActualizado.getDescripcion()).isEqualTo("Credit");
		assertThat(movimientoActualizado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
		assertThat(movimientoActualizado.getUsuarioActualiza()).isEqualTo("admin");
	}
	
	@Test
	@DisplayName("Test para eliminar un movimiento.")
	void test_eliminar_movimiento() {
		Date fechaActualizacion = new Date();
		//given
		catalogoMovimientoRepository.save(abono);
		
		//when
		CatalogoMovimiento movimientoGuardado = catalogoMovimientoRepository.findById(abono.getIdCatMovimiento()).orElse(null);
		movimientoGuardado.setActivo(false);
		movimientoGuardado.setFechaActualizacion(fechaActualizacion);
		movimientoGuardado.setUsuarioActualiza("admin");
		CatalogoMovimiento movimientoActualizado = catalogoMovimientoRepository.save(movimientoGuardado);
		
		//then
		assertThat(movimientoActualizado).isNotNull();
		assertThat(movimientoActualizado.getActivo()).isFalse();
		assertThat(movimientoActualizado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
		assertThat(movimientoActualizado.getUsuarioActualiza()).isEqualTo("admin");
	}
	
	@Test
	@DisplayName("Test para listar los movimientos.")
	void test_listar_movimientos() {
		//given
		CatalogoMovimiento cargo = CatalogoMovimiento.builder()
				.descripcion("Cargo")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
		
		catalogoMovimientoRepository.save(cargo);
		catalogoMovimientoRepository.save(abono);
		
		//when
		Page<CatalogoMovimiento> listaMovimientos = catalogoMovimientoRepository.findAll(PageRequest.of(0, 2, Sort.by("descripcion").ascending()));
		
		//then
		assertThat(listaMovimientos).isNotNull();
		assertThat(listaMovimientos.getSize()).isEqualTo(2);
		assertThat(listaMovimientos.getNumber()).isZero();
		assertThat(listaMovimientos.getNumberOfElements()).isEqualTo(2);
		assertThat(listaMovimientos.getSort().toString()).hasToString("descripcion: ASC");
		assertThat(listaMovimientos.getTotalElements()).isEqualTo(2);
		assertThat(listaMovimientos.getTotalPages()).isEqualTo(1);
		assertThat(listaMovimientos.getContent().get(0).getDescripcion()).isEqualTo("Abono");
	}

	@Test
	@DisplayName("Test para obtener un movimiento.")
	void test_obtener_movimiento_por_id() {
		//given
		catalogoMovimientoRepository.save(abono);
		
		//when
		CatalogoMovimiento movimientoGuardado = catalogoMovimientoRepository.findById(abono.getIdCatMovimiento()).orElse(null);
		
		//then
		assertThat(movimientoGuardado).isNotNull();
		assertThat(movimientoGuardado.getIdCatMovimiento()).isNotNegative();
		assertThat(movimientoGuardado.getActivo()).isTrue();
	}
}
