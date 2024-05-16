package com.sosa.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sosa.model.CatalogoOperacion;

@Repository
public interface CatalogoOperacionRepository extends PagingAndSortingRepository<CatalogoOperacion, Long>{
	
	Optional<CatalogoOperacion> findByDescripcion(String descripcion);
	
}
