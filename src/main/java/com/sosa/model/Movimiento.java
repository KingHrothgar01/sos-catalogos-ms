package com.sosa.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "movimiento")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter 
@Setter
@AllArgsConstructor
@NoArgsConstructor 
@ToString
@Builder
@EqualsAndHashCode
public class Movimiento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_movimiento")
	private long idMovimiento;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.ALL})
	@JoinColumn(name="id_cat_operacion")
	private CatalogoOperacion catalogoOperacion;
	
	@Column(name = "monto", nullable = false)
	private Double monto;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.ALL})
	@JoinColumn(name="id_cat_movimiento")
	private CatalogoMovimiento catalogoMovimiento;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.ALL})
	@JoinColumn(name="id_prestamo")
	private Prestamo prestamo;
	
	@Column(name = "fecha_movimiento", nullable = false)
	private Date fechaMovimiento;
	
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
