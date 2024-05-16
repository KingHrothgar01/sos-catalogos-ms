package com.sosa.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name="catalogo_movimiento")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CatalogoMovimiento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_cat_movimiento")
	private long idCatMovimiento;
	
	@Column(name="descripcion", nullable = false)
	private String descripcion;
	
	@OneToMany(mappedBy = "catalogoMovimiento", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
	private final List<Movimiento> movimientos = new ArrayList<>();
	
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
	
	public void addMovimiento(Movimiento movimiento) {
		this.movimientos.add(movimiento);
		movimiento.setCatalogoMovimiento(this);
	}

	public void removeMovimiento(Movimiento movimiento) {
		this.movimientos.remove(movimiento);
		movimiento.setCatalogoMovimiento(null);
	}

}
