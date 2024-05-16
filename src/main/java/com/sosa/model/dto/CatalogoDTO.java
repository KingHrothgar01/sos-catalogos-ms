package com.sosa.model.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogoDTO extends RepresentationModel<CatalogoDTO> {
	
	@JsonProperty("id-catalogo")
	@JsonSetter("id-catalogo")
	private long idCatalogo;
	
	@NotNull(message = "El tipo de movimiento es requerido.")
	@JsonProperty("descripcion")
	@JsonSetter("descripcion")
	private String descripcion;

	@JsonProperty(value = "fecha-registro", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("fecha-registro")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaRegistro;
	
	//@JsonIgnore
	//@FutureOrPresent(message = "La fecha de actualización tiene que ser mayor o igual a hoy.")
	@JsonProperty(value = "fecha-actualizacion", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("fecha-actualizacion")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaActualizacion;

	@JsonProperty(value = "usuario-registra", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("usuario-registra")
	private String usuarioRegistra;
	
	@JsonProperty(value = "usuario-actualiza", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("usuario-actualiza")
	private String usuarioActualiza;

	@JsonProperty(value = "activo", access = JsonProperty.Access.READ_ONLY)
	@JsonSetter("activo")
	private Boolean activo;
}
