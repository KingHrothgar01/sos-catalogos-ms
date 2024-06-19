package com.sosa.model.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;

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
	
	@NotBlank(message = "La descripci\u00F3n es requerida.")
	@JsonProperty("descripcion")
	@JsonSetter("descripcion")
	private String descripcion;

	@JsonProperty(value = "fecha-registro")
	@JsonSetter("fecha-registro")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaRegistro;

	@JsonProperty(value = "fecha-actualizacion")
	@JsonSetter("fecha-actualizacion")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date fechaActualizacion;

	@JsonProperty(value = "usuario-registra")
	@JsonSetter("usuario-registra")
	private String usuarioRegistra;
	
	@JsonProperty(value = "usuario-actualiza")
	@JsonSetter("usuario-actualiza")
	private String usuarioActualiza;

	@JsonProperty(value = "activo")
	@JsonSetter("activo")
	private Boolean activo;
}
