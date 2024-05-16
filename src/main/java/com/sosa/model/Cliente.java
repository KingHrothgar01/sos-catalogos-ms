package com.sosa.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cliente")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Cliente {
	
	@Id
	@Column(name="id_cliente")
	private String idCliente;
	
	@Column(name="nombre", nullable = false)
	private String nombre;
	
	@Column(name="apellido_paterno", nullable = false)
	private String apellidoPaterno;
	
	@Column(name="apellido_materno", nullable = false)
	private String apellidoMaterno;
	
	@Column(name="rfc", nullable = false)
	private String rfc;
	
	@Column(name="curp", nullable = true)
	private String curp;
	
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, orphanRemoval = true)
	private final List<Prestamo> prestamos = new ArrayList<>();
	
	@Column(name = "fecha_registro", nullable = false)
	private Date fechaRegistro;
	
	@Column(name = "fecha_actualizacion", nullable = true)
	private Date fechaActualizacion;

	@Column(name = "usuario_registra", nullable = false)
	private String usuarioRegistra;
	
	@Column(name = "usuario_actualiza", nullable = true)
	private String usuarioActualiza;

	@Column(name="activo")
	private Boolean activo;
}
