package com.sosa.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
@Table(name="tasa_interes")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TasaInteres {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_tasa_interes")
	private long idTasaInteres;
	
	@Column(name="cat_sin_iva", nullable = false)
	private Double catSinIva;
	
	@Column(name="anual_ordinaria", nullable = false)
	private Double anualOrdinaria;
	
	@Column(name="anual_moratoria", nullable = true)
	private Double anualMoratoria;
	
	@OneToOne(mappedBy = "tasaInteres", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
	private EstadoCuenta estadoCuenta;
	
	@Column(name = "fecha_registro", nullable = false)
	private Date fechaRegistro;
	
	@Column(name = "fecha_actualizacion", nullable = true)
	private Date fechaActualizacion;

	@Column(name = "usuario_registra", nullable = false)
	private String usuarioRegistra;
	
	@Column(name = "usuario_actualiza", nullable = true)
	private String usuarioActualiza;
	
	@Column(name = "activo", nullable = true)
	private Boolean activo;

}
