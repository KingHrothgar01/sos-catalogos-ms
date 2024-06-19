package com.sosa.controller;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_002;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_004;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_008;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_010;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_012;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_013;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CM_014;
import static com.sosa.util.Constants.HTTP_MSG_400;
import static com.sosa.util.Constants.HTTP_MSG_404;
import static com.sosa.util.Constants.HTTP_MSG_500;
import static com.sosa.util.Constants.USER_ALTA;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosa.event.GlobalExceptionHandler;
import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.exception.HTTP500Exception;
import com.sosa.model.CatalogOperationModelAssembler;
import com.sosa.model.dto.CatalogoDTO;
import com.sosa.model.dto.PagingDTO;
import com.sosa.service.CatalogoOperacionService;

@WebMvcTest(controllers = CatalogoOperacionControllerTests.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {CatalogoOperacionController.class, GlobalExceptionHandler.class, CatalogOperationModelAssembler.class})
public class CatalogoOperacionControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CatalogoOperacionService operacionService;
	
	@MockBean
	private PagedResourcesAssembler<CatalogoDTO> pagedResourcesAssembler;
	
	private CatalogoDTO dto;
	
	LocalDateTime localDateTime = LocalDateTime.of(2024, Month.JANUARY, 1, 10, 10, 30);
	Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
	Date date = Date.from(instant);

	@BeforeEach
	void setup() {
		
		dto = CatalogoDTO.builder()
				.idCatalogo(1)
				.descripcion("Abono")
				.fechaRegistro(date)
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(true)
				.build();
	}

	
	@Test
	@DisplayName("Test para manejar la excepción cu\u00E1ndo la DB es inalcanzable.")
	void test_db_sin_conexion() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(operacionService.saveOperacion(any(CatalogoDTO.class))).thenThrow(CannotCreateTransactionException.class);
		
		ResultActions response = mockMvc.perform(post("/prestamos/v1/catalogos/operaciones")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para guardar una operaci\u00F3n en el cat\u00E1logo.")
	void test_crear_operacion() throws Exception{
		// given
		given(operacionService.saveOperacion(any(CatalogoDTO.class)))
			.willAnswer((invocation) -> Optional.ofNullable(invocation.getArgument(0)));
		
		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/catalogos/operaciones")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print()).andExpect(status().isCreated())
			.andExpect(content().contentType("application/json"))
			.andExpect(header().exists("Location"))
			.andExpect(jsonPath("$.descripcion", is(dto.getDescripcion())))
			.andExpect(jsonPath("$.activo", is(dto.getActivo())));
	}
	
	@Test
	@DisplayName("Test para guardar una operaci\u00F3n en el cat\u00E1logo, DTO nulo - Escenario de error 1.")
	void test_crear_operacion_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(post("/prestamos/v1/catalogos/operaciones")
			   .contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
			    .andExpect(result -> assertEquals("Required request body is missing: public com.sosa.model.dto.CatalogoDTO com.sosa.controller.CatalogoOperacionController.createOperacion(com.sosa.model.dto.CatalogoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar una operaci\u00F3n en el cat\u00E1logo, Descripci\u00f3n nula - Escenario de error 2.")
	void test_crear_operacion_error_2() throws Exception {
		// given
		dto.setDescripcion(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/catalogos/operaciones")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.CatalogoDTO com.sosa.controller.CatalogoOperacionController.createOperacion(com.sosa.model.dto.CatalogoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'catalogoDTO' on field 'descripcion': rejected value [null]; codes [NotBlank.catalogoDTO.descripcion,NotBlank.descripcion,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [catalogoDTO.descripcion,descripcion]; arguments []; default message [descripcion]]; default message [La descripci\u00F3n es requerida.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar una operaci\u00F3n en el cat\u00E1logo, Descripci\\u00f3n vac\u00eda - Escenario de error 3.")
	void test_crear_operacion_error_3() throws Exception {
		// given
		dto.setDescripcion(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(post("/prestamos/v1/catalogos/operaciones")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [0] in public com.sosa.model.dto.CatalogoDTO com.sosa.controller.CatalogoOperacionController.createOperacion(com.sosa.model.dto.CatalogoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'catalogoDTO' on field 'descripcion': rejected value [ ]; codes [NotBlank.catalogoDTO.descripcion,NotBlank.descripcion,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [catalogoDTO.descripcion,descripcion]; arguments []; default message [descripcion]]; default message [La descripci\u00F3n es requerida.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_002)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para guardar una operaci\u00F3n en el cat\u00E1logo, Error en capa de servicio - Escenario de error 4.")
	void test_crear_operacion_error_4() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		// given
		given(operacionService.saveOperacion(any(CatalogoDTO.class))).willReturn(Optional.empty());

		// when
		ResultActions response = mockMvc.perform(post("/prestamos/v1/catalogos/operaciones")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().is5xxServerError())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP500Exception))
			    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_CM_012, result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_012)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));

	}
	
	@Test
	@DisplayName("Test para manejar la excepci\u00F3n cu\u00E1ndo la operaci\u00F3n ya existe en el cat\u00E1logo - Escenario de error 5.")
	void test_crear_operacion_error_5() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(operacionService.saveOperacion(any(CatalogoDTO.class))).thenThrow(HTTP400Exception.class);
		
		ResultActions response = mockMvc.perform(post("/prestamos/v1/catalogos/operaciones")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar una operaci\u00F3n en el cat\u00e1logo.")
	void test_actualizar_operacion() throws Exception {
		// given
		given(operacionService.updateOperacion(any(CatalogoDTO.class)))
				.willAnswer((invocation) -> Optional.ofNullable(invocation.getArgument(0)));
		
		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", "1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto)));

		// then
		response.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.descripcion", is(dto.getDescripcion())))
			.andExpect(jsonPath("$.activo", is(dto.getActivo())));
	}
	
	@Test
	@DisplayName("Test para actualizar una operaci\u00F3n en el cat\u00e1logo, DTO nulo - Escenario de error 1.")
	void test_actualizar_operacion_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
			    .andExpect(result -> assertEquals("Required request body is missing: public com.sosa.model.dto.CatalogoDTO com.sosa.controller.CatalogoOperacionController.updateOperacion(java.lang.Long,com.sosa.model.dto.CatalogoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar una operaci\u00F3n en el cat\u00e1logo, Descripci\u00f3n nula - Escenario de error 2.")
	void test_actualizar_operacion_error_2() throws Exception {
		// given
		dto.setDescripcion(null);

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.CatalogoDTO com.sosa.controller.CatalogoOperacionController.updateOperacion(java.lang.Long,com.sosa.model.dto.CatalogoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'catalogoDTO' on field 'descripcion': rejected value [null]; codes [NotBlank.catalogoDTO.descripcion,NotBlank.descripcion,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [catalogoDTO.descripcion,descripcion]; arguments []; default message [descripcion]]; default message [La descripci\u00F3n es requerida.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar una operaci\u00F3n en el cat\u00E1logo, Descripci\u00f3n vac\u00eda - Escenario de error 3.")
	void test_actualizar_operacion_error_3() throws Exception {
		// given
		dto.setDescripcion(" ");

		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 1)
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
			    .andExpect(result -> assertEquals("Validation failed for argument [1] in public com.sosa.model.dto.CatalogoDTO com.sosa.controller.CatalogoOperacionController.updateOperacion(java.lang.Long,com.sosa.model.dto.CatalogoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse): [Field error in object 'catalogoDTO' on field 'descripcion': rejected value [ ]; codes [NotBlank.catalogoDTO.descripcion,NotBlank.descripcion,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [catalogoDTO.descripcion,descripcion]; arguments []; default message [descripcion]]; default message [La descripci\u00F3n es requerida.]] ", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar una operaci\u00F3n en el cat\u00E1logo, Objeto actualizado vac\u00edo - Escenario de error 4.")
	void test_actualizar_operacion_error_4() throws Exception {
		// given
		given(operacionService.updateOperacion(any(CatalogoDTO.class))).willReturn(Optional.empty());
		
		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto)));

		// then
		response.andDo(print())
		        .andExpect(status().is5xxServerError())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP500Exception))
			    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_CM_013, result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_005)))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo no coincide el identificador del objeto a actualizar.")
	void test_actualizar_operacion_error_5() throws Exception{
		// given
		
		// when
		ObjectMapper mapper = new ObjectMapper();
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 2)
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		// then
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP400Exception))
		    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_CM_005, result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_005)))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)));
	}
	
	@Test
	@DisplayName("Test para actualizar una operaci\u00F3n en el cat\u00E1logo, No existe la operaci\u00F3n - Escenario de error 6.")
	void test_actualizar_operacion_error_6() throws Exception{
		// given
		when(operacionService.updateOperacion(any(CatalogoDTO.class))).thenThrow(HTTP404Exception.class);
		
		// when
		ObjectMapper mapper = new ObjectMapper();		
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		//then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepci\u00f3n cu\u00e1ndo la DB es inalcanzable.")
	void test_actualizar_operacion_error_7() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(operacionService.updateOperacion(any(CatalogoDTO.class))).thenThrow(CannotCreateTransactionException.class);
		
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para hacer un borrado l\u00F3gico de una operaci\u00F3n del cat\u00E1logo.")
	void test_eliminar_operacion() throws Exception {
		// given
		LocalDateTime deletedDateTime = LocalDateTime.of(2024, Month.JANUARY, 1, 10, 10, 30);
		Instant instant = deletedDateTime.atZone(ZoneId.systemDefault()).toInstant();
		Date deletedDate = Date.from(instant);
		
		CatalogoDTO deleted = CatalogoDTO.builder()
			.idCatalogo(1)
			.descripcion("Abono")
			.fechaRegistro(date)
			.fechaActualizacion(deletedDate)
			.usuarioRegistra(USER_ALTA)
			.usuarioActualiza(USER_ALTA)
			.activo(false)
			.build();
		
		given(operacionService.deleteOperacion(anyLong())).willReturn(Optional.of(deleted));
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/catalogos/operaciones/{id}", 1));

		// then
		response.andDo(print()).andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.descripcion", is(deleted.getDescripcion())))
			.andExpect(jsonPath("$.fecha-actualizacion", is("2024-01-01T16:10:30")))
			.andExpect(jsonPath("$.usuario-actualiza", is("admin")))
			.andExpect(jsonPath("$.activo", is(false)));
	}
	
	@Test
	@DisplayName("Test para hacer un borrado l\u00F3gico de una operaci\u00F3n del cat\u00E1logo, id negativo - Escenario de error 1.")
	void test_eliminar_operacion_error_1() throws Exception {
		// given
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/catalogos/operaciones/{id}", -1)
				.contentType(MediaType.APPLICATION_JSON));

		// then
		response.andDo(print())
	        .andExpect(status().isBadRequest())
	        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
		    .andExpect(result -> assertEquals("deleteOperacion.id: El identificador no puede ser negativo.", result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
		    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_008)));
	}
	
	@Test
	@DisplayName("Test para hacer un borrado l\u00F3gico de una operaci\u00F3n del cat\u00E1logo, error en capa de servicio - Escenario de error 2.")
	void test_eliminar_operacion_error_2() throws Exception {
		// given
		given(operacionService.deleteOperacion(anyLong())).willReturn(Optional.empty());
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/catalogos/operaciones/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON));

		// then
		response.andDo(print())
		        .andExpect(status().is5xxServerError())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP500Exception))
			    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_CM_014, result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_014)));
	}
	
	@Test
	@DisplayName("Test para hacer un borrado l\u00F3gico de una operaci\u00F3n del cat\u00E1logo, no existe la operaci\u00F3n - Escenario de error 3.")
	void test_eliminar_operacion_error_3() throws Exception{
		// given
		when(operacionService.deleteOperacion(anyLong())).thenThrow(HTTP404Exception.class);
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/catalogos/operaciones/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para hacer un borrado l\u00F3gico de una operaci\u00F3n del cat\u00E1logo, id vacío - Escenario de error 4.")
	void test_eliminar_operacion_error_4() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/catalogos/operaciones/{id}", ""));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpRequestMethodNotSupportedException))
			    .andExpect(result -> assertEquals("Request method 'DELETE' not supported", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_008)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo la DB es inalcanzable.")
	void test_eliminar_cliente_error_5() throws Exception{
		// given
		when(operacionService.deleteOperacion(anyLong())).thenThrow(CannotCreateTransactionException.class);
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/catalogos/operaciones/{id}", 1));
		
		// then
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para obtener una operaci\u00F3n.")
	void test_obtener_operacion() throws Exception {
		// given
		given(operacionService.findOperacion(anyLong())).willReturn(Optional.of(dto));
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.descripcion", is("Abono")))
		        .andExpect(jsonPath("$.usuario-registra", is(USER_ALTA)))
		        .andExpect(jsonPath("$.fecha-registro").value("2024-01-01T16:10:30"))
		        .andExpect(jsonPath("$.activo").value("true"));
				
	}
	
	@Test
	@DisplayName("Test para obtener una operaci\u00F3n, id negativo - Escenario de error 1.")
	void test_obtener_operacion_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones/{id}", -1)
				.contentType(MediaType.APPLICATION_JSON));

		// then
		response.andDo(print())
	        .andExpect(status().isBadRequest())
		    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
		    .andExpect(result -> assertEquals("getOperacion.id: El identificador no puede ser negativo.", result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
		    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_004)));
	}
	
	@Test
	@DisplayName("Test para obtener una operaci\u00F3n del cat\u00E1logo, no existe la operaci\u00F3n - Escenario de error 2.")
	void test_obtener_operacion_error_2() throws Exception{
		// given
		when(operacionService.findOperacion(anyLong())).thenThrow(HTTP404Exception.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones/{id}", 2));
		
		// then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
			.andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para obtener una operaci\u00F3n del cat\u00E1logo, no existe la operaci\u00F3n - Escenario de error 3.")
	void test_obtener_operacion_error_3() throws Exception {
		// given
		given(operacionService.findOperacion(anyLong())).willReturn(Optional.empty());
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones/{id}", 2)
				.contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
	        .andExpect(status().isNotFound())
		    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP404Exception))
		    .andExpect(result -> assertEquals(HTTP_MSG_404, result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("Test para manejar la excepción cuándo la DB es inalcanzable.")
	void test_obtener_operacion_error_4() throws Exception{
		// given
		when(operacionService.findOperacion(anyLong())).thenThrow(CannotCreateTransactionException.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones/{id}", 2));
		
		// then
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CannotCreateTransactionException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Test para listar operaciones del cat\u00E1logo")
	void test_listar_operaciones() throws Exception{
		PagingDTO dto = new PagingDTO();
			dto.setPage(0);
			dto.setSize(10);
			dto.setOrder(Direction.ASC);
			dto.setProperty("idCatOperacion");
		
		// given
		List<CatalogoDTO> listaOperaciones = new ArrayList<>();
		listaOperaciones.add(CatalogoDTO.builder().idCatalogo(1).descripcion("Abono").fechaRegistro(new Date()).usuarioRegistra("admin").build());
		listaOperaciones.add(CatalogoDTO.builder().idCatalogo(2).descripcion("Cargo").fechaRegistro(new Date()).usuarioRegistra("admin").build());
		PageImpl<CatalogoDTO> page = new PageImpl<CatalogoDTO>(listaOperaciones, PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()), 2);
		
		Link l[] = new Link[] {
				Link.of("http://localhost:8080/prestamos/v1/catalogos/operaciones?sortOrder=ASC&page=0&size=10&sort=idOperacion")};
		PagedModel<CatalogoDTO> model = PagedModel.of(listaOperaciones, new PageMetadata(10, 0, 2, 1), l);
		
		given(operacionService.findAllOperaciones(any(PagingDTO.class))).willReturn(page);
		given(pagedResourcesAssembler.toModel(any(Page.class), any(CatalogOperationModelAssembler.class))).willReturn(model);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones")
				.param("page", dto.getPage().toString())
				.param("size", dto.getSize().toString())
				.param("property", dto.getProperty())
				.param("sortOrder", dto.getOrder().toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.page.size", is(dto.getSize())))
		        .andExpect(jsonPath("$.page.number", is(dto.getPage())));
		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Test para listar operaciones del cat\u00E1logo, no existen operaciones - Escenario de error 1. - lista vacía.")
	void test_listar_operaciones_error_1() throws Exception{
		PagingDTO dto = new PagingDTO();
		dto.setPage(0);
		dto.setSize(10);
		dto.setOrder(Direction.ASC);
		dto.setProperty("idCatOperacion");
		
		// given
		List<CatalogoDTO> listaOperaciones = new ArrayList<>();
		PageImpl<CatalogoDTO> page = new PageImpl<CatalogoDTO>(listaOperaciones, PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()), 0);
		
		Link l[] = new Link[] {
				Link.of("http://192.168.100.65:8092/prestamos/v1/catalogos/operaciones?page=0&size=10&sort=idCatOperacion,asc")};
		PagedModel<CatalogoDTO> model = PagedModel.of(listaOperaciones, new PageMetadata(10, 0, 0, 0), l);
		
		given(operacionService.findAllOperaciones(any(PagingDTO.class))).willReturn(page);
		given(pagedResourcesAssembler.toModel(any(Page.class), any(CatalogOperationModelAssembler.class))).willReturn(model);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones")
				.param("page", dto.getPage().toString())
				.param("size", dto.getSize().toString())
				.param("property", dto.getProperty())
				.param("sortOrder", dto.getOrder().toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.page.size", is(10)))
		        .andExpect(jsonPath("$.page.number", is(0)))
		        .andExpect(jsonPath("$.page.totalElements", is(0)))
		        .andExpect(jsonPath("$.page.totalPages", is(0)));
	}
	
	@Test
	@DisplayName("Test para manejar una excepción en la capa de servicio.")
	void test_listar_clientes_error_2() throws Exception{
		// given
		when(operacionService.findAllOperaciones(any(PagingDTO.class))).thenThrow(RuntimeException.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones")
			.param("page", "0")
			.param("size", "10")
			.param("property", "idCatOperacion")
			.param("sortOrder", Direction.ASC.toString()));
		
		// then
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("Test para listar operaciones del cat\u00E1logo, n\u00FAmero de p\u00E1gina menor a 0 - Escenario de error 3.")
	void test_listar_clientes_error_3() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones")
			.param("page", "-1")
			.param("size", "2")
			.param("property", "idCatOperacion")
			.param("sortOrder", Direction.ASC.toString()));
		
		// then
		response.andDo(print())
	        .andExpect(status().isBadRequest())
		    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
		    .andExpect(result -> assertEquals("getAllItems.page: El numero de p\u00E1gina debe ser mayor o igual a cero.", result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
		    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_010)));
	}
	
	@Test
	@DisplayName("Test para listar operaciones del cat\u00E1logo, tama\u00E1o de p\u00E1gina menor a 0 - Escenario de error 4.")
	void test_listar_clientes_error_4() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones")
				.param("page", "0")
				.param("size", "-1")
				.param("property", "idCatOperacion")
				.param("sortOrder", Direction.ASC.toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
			    .andExpect(result -> assertEquals("getAllItems.size: El tama\u00F1o de p\u00E1gina debe ser mayor a cero.", result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_010)));
	}
	
	@Test
	@DisplayName("Test para listar operaciones del cat\u00E1logo, campo incorrecto - Escenario de error 5.")
	void test_listar_clientes_error_5() throws Exception {
		// given
		when(operacionService.findAllOperaciones(any(PagingDTO.class))).thenThrow(PropertyReferenceException.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones")
				.param("page", "0")
				.param("size", "10")
				.param("property", "operacion")
				.param("sortOrder", Direction.ASC.toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof PropertyReferenceException))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_010)));
	}
	
	@Test
	@DisplayName("Test para listar operaciones del cat\u00E1logo, ordenaci\u00F3n incorrecta - Escenario de error 6.")
	void test_listar_clientes_error_6() throws Exception {
		// given
		when(operacionService.findAllOperaciones(any(PagingDTO.class))).thenThrow(IllegalArgumentException.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones")
				.param("page", "0")
				.param("size", "10")
				.param("property", "operacion")
				.param("sortOrder", "ASF"));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
			    .andExpect(jsonPath("$.error-message", is(HTTP_MSG_400)))
			    .andExpect(jsonPath("$.error-detail", is(BUSINESS_MSG_ERR_CM_010)));
	}
}
