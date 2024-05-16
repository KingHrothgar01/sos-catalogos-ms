package com.sosa.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sosa.model.CatalogoMovimiento;

@Repository
public interface CatalogoMovimientoRepository extends PagingAndSortingRepository<CatalogoMovimiento, Long>{
	
	Optional<CatalogoMovimiento> findByDescripcion(String descripcion);
	
}
