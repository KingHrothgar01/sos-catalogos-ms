package com.sosa.model.dto;

import java.io.Serializable;

import org.springframework.data.domain.Sort.Direction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PagingDTO implements Serializable{

	private static final long serialVersionUID = -2260953493126523728L;
	
	private Integer page; 
	private Integer size; 
	private Direction order; 
	private String property;

}
