package com.sosa.model;

import java.time.LocalDate;
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
@Table(name = "estado_cuenta")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor 
@ToString
@Builder
public class EstadoCuenta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_edo_cuenta")
	private long idEstadoCuenta;
	
	@Column(name = "fecha_inicio_periodo", nullable = false)
	private LocalDate fechaInicioPeriodo;
	
	@Column(name = "fecha_fin_periodo", nullable = false)
	private LocalDate fechaFinPeriodo;
	
	@Column(name = "fecha_limite_pago", nullable = false)
	private LocalDate fechaLimitePago;
	
	@Column(name = "saldo_insoluto_inicial", nullable = false)
	private Double saldoInsolutoInicial;
	
	@Column(name = "saldo_promedio_mes", nullable = false)
	private Double saldoPromedioMes;
	
	@Column(name = "intereses_ordinarios", nullable = false)
	private Double interesesOrdinarios;

	@Column(name = "comisiones", nullable = false)
	private Double comisiones;
	
	@Column(name = "iva", nullable = false)
	private Double iva;
	
	@Column(name = "disposiciones", nullable = false)
	private Double disposiciones;

	@Column(name = "pagos", nullable = false)
	private Double pagos;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.ALL})
	@JoinColumn(name="id_prestamo")
	private Prestamo prestamo;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.ALL})
	@JoinColumn(name="id_tasa_interes")
	private TasaInteres tasaInteres;
	
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
