package com.sosa.controller;

import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_005;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_008;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_010;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_012;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_013;
import static com.sosa.util.Constants.BUSINESS_MSG_ERR_CO_014;
import static com.sosa.util.Constants.HTTP_MSG_400;
import static com.sosa.util.Constants.HTTP_MSG_404;
import static com.sosa.util.Constants.HTTP_MSG_500;
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

	@BeforeEach
	void setup() {
		LocalDateTime localDateTime = LocalDateTime.of(2024, Month.JANUARY, 1, 10, 10, 30);
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		Date date = Date.from(instant);
		
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
	@DisplayName("Test para manejar la excepción cuándo la DB es inalcanzable.")
	void test_db_sin_conexion() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(operacionService.saveOperacion(any(CatalogoDTO.class))).thenThrow(CannotCreateTransactionException.class);
		
		ResultActions response = mockMvc.perform(post("/prestamos/v1/catalogos/operaciones")
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().is5xxServerError())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.errorDetail", is("Error en servidor.")));
	}
	
	@Test
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
	void test_crear_operacion_error_1() throws Exception {
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
			    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_CO_012, result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_500)));
	}
	
	@Test
	void test_actualizar_operacion() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		// given
		given(operacionService.updateOperacion(any(CatalogoDTO.class)))
				.willAnswer((invocation) -> Optional.ofNullable(invocation.getArgument(0)));
		
		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", "1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto)));

		// then
		response.andExpect(status().isOk())
			.andDo(print())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.descripcion", is(dto.getDescripcion())))
			.andExpect(jsonPath("$.activo", is(dto.getActivo())));
	}
	
	@Test
	void test_actualizar_operacion_error_1() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		// given
		
		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 2)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto)));

		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest());
		
		response.andDo(print())
	        .andExpect(status().isBadRequest())
		    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP400Exception))
		    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_CO_005, result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_400)));
	}
	
	@Test
	void test_actualizar_operacion_error_2() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON));

		// then
		response.andDo(print())
	        .andExpect(status().isBadRequest())
		    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
		    .andExpect(result -> assertEquals("Required request body is missing: public com.sosa.model.dto.CatalogoDTO com.sosa.controller.CatalogoOperacionController.updateOperacion(java.lang.Long,com.sosa.model.dto.CatalogoDTO,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)", result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_400)));
	}
	
	@Test
	void test_actualizar_operacion_error_3() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		// given
		given(operacionService.updateOperacion(any(CatalogoDTO.class))).willReturn(Optional.empty());
		
		// when
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(dto)));

		// then
		response.andDo(print())
		        .andExpect(status().is5xxServerError())
			    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP500Exception))
			    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_CO_013, result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("")
	void test_actualizar_operacion_error_4() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(operacionService.updateOperacion(any(CatalogoDTO.class))).thenThrow(HTTP404Exception.class);
		
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("")
	void test_actualizar_operacion_error_5() throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		
		when(operacionService.updateOperacion(any(CatalogoDTO.class))).thenThrow(HTTP400Exception.class);
		
		ResultActions response = mockMvc.perform(put("/prestamos/v1/catalogos/operaciones/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON)
			    .content(mapper.writeValueAsString(dto)));
		
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_400)));
	}
	
	@Test
	void test_eliminar_operacion() throws Exception {
		// given
		CatalogoDTO disabled = CatalogoDTO.builder()
				.idCatalogo(1)
				.descripcion("Abono")
				.fechaRegistro(new Date())
				.fechaActualizacion(null)
				.usuarioRegistra("admin")
				.usuarioActualiza(null)
				.activo(false)
				.build();
		
		given(operacionService.deleteOperacion(anyLong())).willReturn(Optional.of(disabled));
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/catalogos/operaciones/{id}", "1")
				.contentType(MediaType.APPLICATION_JSON));

		// then
		response.andExpect(status().isOk())
			.andDo(print())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.descripcion", is(dto.getDescripcion())))
			.andExpect(jsonPath("$.activo", is(false)));
	}
	
	@Test
	void test_eliminar_operacion_error_1() throws Exception {
		// given
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/catalogos/operaciones/{id}", -1)
				.contentType(MediaType.APPLICATION_JSON));

		// then
		response.andDo(print())
	        .andExpect(status().isBadRequest())
		    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP400Exception))
		    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_CO_008, result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_400)));
	}
	
	@Test
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
			    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_CO_014, result.getResolvedException().getMessage()))
			    .andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_500)));
	}
	
	@Test
	@DisplayName("")
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
			.andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("")
	void test_eliminar_operacion_error_4() throws Exception{
		// given
		when(operacionService.deleteOperacion(anyLong())).thenThrow(HTTP400Exception.class);
		
		// when
		ResultActions response = mockMvc.perform(delete("/prestamos/v1/catalogos/operaciones/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_400)));
	}
	
	@Test
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
		        .andExpect(jsonPath("$.usuario-registra", is("admin")))
		        .andExpect(jsonPath("$.fecha-registro").value("2024-01-01T16:10:30"))
		        .andExpect(jsonPath("$.activo").value("true"));
	}
	
	@Test
	void test_obtener_operacion_error_1() throws Exception {
		// given

		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones/{id}", -1)
				.contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest());

		// then
		response.andDo(print())
	        .andExpect(status().isBadRequest())
		    .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP400Exception))
		    .andExpect(result -> assertEquals(BUSINESS_MSG_ERR_CO_010, result.getResolvedException().getMessage()))
		    .andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_400)));
	}
	
	@Test
	void test_obtener_operacion_error_2() throws Exception {
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
		    .andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_404)));
				
	}
	
	@Test
	@DisplayName("")
	void test_obtener_operacion_error_3() throws Exception{
		// given
		when(operacionService.findOperacion(anyLong())).thenThrow(HTTP404Exception.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_404)));
	}
	
	@Test
	@DisplayName("")
	void test_obtener_operacion_error_4() throws Exception{
		// given
		when(operacionService.findOperacion(anyLong())).thenThrow(HTTP400Exception.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones/{id}", 1)
			    .contentType(MediaType.APPLICATION_JSON));
		
		// then
		response.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.errorDetail", is(HTTP_MSG_400)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void test_listar_operaciones() throws Exception{
		PagingDTO dto = new PagingDTO();
			dto.setPage(0);
			dto.setSize(1);
			dto.setOrder(Direction.ASC);
			dto.setProperty("idCatOperacion");
		
		// given
		List<CatalogoDTO> listaOperaciones = new ArrayList<>();
		listaOperaciones.add(CatalogoDTO.builder().idCatalogo(1).descripcion("Abono").fechaRegistro(new Date()).usuarioRegistra("admin").activo(true).build());
		listaOperaciones.add(CatalogoDTO.builder().idCatalogo(2).descripcion("Cargo").fechaRegistro(new Date()).usuarioRegistra("admin").activo(true).build());
		PageImpl<CatalogoDTO> page = new PageImpl<CatalogoDTO>(listaOperaciones, PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(dto.getProperty()).ascending()), 2);
		
		Link l[] = new Link[] {
				Link.of("http://localhost:8080/prestamos/v1/catalogos/operaciones?sortOrder=ASC&page=0&size=2&sort=idCatOperacion")};
		PagedModel<CatalogoDTO> model = PagedModel.of(listaOperaciones, new PageMetadata(1, 0, 2, 2), l);
		
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
	void test_listar_operaciones_error_1() throws Exception{
		PagingDTO dto = new PagingDTO();
		dto.setPage(0);
		dto.setSize(1);
		dto.setOrder(Direction.ASC);
		dto.setProperty("idCatOperacion");
		
		// given
		when(operacionService.findAllOperaciones(any(PagingDTO.class))).thenThrow(HTTP400Exception.class);
		
		// when
		ResultActions response = mockMvc.perform(get("/prestamos/v1/catalogos/operaciones")
				.param("page", dto.getPage().toString())
				.param("size", dto.getSize().toString())
				.param("property", dto.getProperty())
				.param("sortOrder", dto.getOrder().toString()));
		
		// then
		response.andDo(print())
		        .andExpect(status().isBadRequest())
		        .andExpect(result -> assertTrue(result.getResolvedException() instanceof HTTP400Exception))
			    .andExpect(jsonPath("$.errorDetail", is("Error en petición cliente.")));
		
	}

}
