package com.sosa.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sosa.exception.HTTP400Exception;
import com.sosa.exception.HTTP401Exception;
import com.sosa.exception.HTTP403Exception;
import com.sosa.exception.HTTP404Exception;
import com.sosa.exception.HTTP500Exception;
import com.sosa.model.dto.CatalogoDTO;
import com.sosa.util.ErrorResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("Event Classes Tests")
class EventTests {

	@Nested
	@DisplayName("CatalogoServiceEvent Tests")
	class CatalogoServiceEventTests {

		private CatalogoServiceEvent event;
		private CatalogoDTO catalogoDTO;
		private Object source;
		private String eventType;

		@BeforeEach
		void setup() {
			source = new Object();
			eventType = "CREATE";
			catalogoDTO = CatalogoDTO.builder()
					.idCatalogo(1)
					.descripcion("Comision")
					.fechaRegistro(new Date())
					.usuarioRegistra("admin")
					.activo(true)
					.build();
		}

		@Test
		@DisplayName("Test CatalogoServiceEvent constructor")
		void test_catalogo_service_event_constructor() {
			event = new CatalogoServiceEvent(source, eventType, catalogoDTO);

			assertThat(event).isNotNull();
			assertThat(event.getSource()).isEqualTo(source);
			assertThat(event.getEventType()).isEqualTo(eventType);
			assertThat(event.getCatalogo()).isEqualTo(catalogoDTO);
		}

		@Test
		@DisplayName("Test CatalogoServiceEvent getters")
		void test_catalogo_service_event_getters() {
			event = new CatalogoServiceEvent(source, eventType, catalogoDTO);

			assertThat(event.getEventType()).isEqualTo("CREATE");
			assertThat(event.getCatalogo()).isNotNull();
			assertThat(event.getCatalogo().getIdCatalogo()).isEqualTo(1);
			assertThat(event.getCatalogo().getDescripcion()).isEqualTo("Comision");
		}

		@Test
		@DisplayName("Test CatalogoServiceEvent setters")
		void test_catalogo_service_event_setters() {
			event = new CatalogoServiceEvent(source, "CREATE", catalogoDTO);

			String newEventType = "UPDATE";
			CatalogoDTO newCatalogo = CatalogoDTO.builder()
					.idCatalogo(2)
					.descripcion("Impuesto")
					.build();

			event.setEventType(newEventType);
			event.setCatalogo(newCatalogo);

			assertThat(event.getEventType()).isEqualTo(newEventType);
			assertThat(event.getCatalogo()).isEqualTo(newCatalogo);
		}

		@Test
		@DisplayName("Test CatalogoServiceEvent toString")
		void test_catalogo_service_event_toString() {
			event = new CatalogoServiceEvent(source, eventType, catalogoDTO);

			String result = event.toString();

			assertThat(result).isNotNull();
			assertThat(result).contains("ClienteServiceEvent");
			assertThat(result).contains("eventType");
		}

		@Test
		@DisplayName("Test CatalogoServiceEvent with DELETE event type")
		void test_catalogo_service_event_delete_type() {
			event = new CatalogoServiceEvent(source, "DELETE", catalogoDTO);

			assertThat(event.getEventType()).isEqualTo("DELETE");
			assertThat(event.getCatalogo()).isNotNull();
		}

		@Test
		@DisplayName("Test CatalogoServiceEvent with null catalog")
		void test_catalogo_service_event_null_catalog() {
			event = new CatalogoServiceEvent(source, eventType, null);

			assertThat(event.getCatalogo()).isNull();
			assertThat(event.getEventType()).isEqualTo(eventType);
		}

		@Test
		@DisplayName("Test CatalogoServiceEvent serialVersionUID")
		void test_catalogo_service_event_serial_version_uid() {
			event = new CatalogoServiceEvent(source, eventType, catalogoDTO);

			long serialVersionUID = -6121624308782503383L;
			assertThat(serialVersionUID).isEqualTo(-6121624308782503383L);
		}
	}

	@Nested
	@DisplayName("CatalogoEventListener Tests")
	class CatalogoEventListenerTests {

		private CatalogoEventListener listener;
		private CatalogoServiceEvent event;
		private CatalogoDTO catalogoDTO;

		@BeforeEach
		void setup() {
			listener = new CatalogoEventListener();
			catalogoDTO = CatalogoDTO.builder()
					.idCatalogo(1)
					.descripcion("Comision")
					.build();
			event = new CatalogoServiceEvent(new Object(), "CREATE", catalogoDTO);
		}

		@Test
		@DisplayName("Test CatalogoEventListener listens to events")
		void test_catalogo_event_listener_on_event() {
			assertThat(listener).isNotNull();
			assertThat(listener).isInstanceOf(
					org.springframework.context.ApplicationListener.class);
		}

		@Test
		@DisplayName("Test CatalogoEventListener onApplicationEvent with CREATE event")
		void test_catalogo_event_listener_on_application_event_create() {
			listener.onApplicationEvent(event);

			assertThat(event.getEventType()).isEqualTo("CREATE");
			assertThat(event.getCatalogo()).isNotNull();
		}

		@Test
		@DisplayName("Test CatalogoEventListener onApplicationEvent with UPDATE event")
		void test_catalogo_event_listener_on_application_event_update() {
			CatalogoServiceEvent updateEvent = new CatalogoServiceEvent(
					new Object(), "UPDATE", catalogoDTO);

			listener.onApplicationEvent(updateEvent);

			assertThat(updateEvent.getEventType()).isEqualTo("UPDATE");
		}

		@Test
		@DisplayName("Test CatalogoEventListener onApplicationEvent with DELETE event")
		void test_catalogo_event_listener_on_application_event_delete() {
			CatalogoServiceEvent deleteEvent = new CatalogoServiceEvent(
					new Object(), "DELETE", catalogoDTO);

			listener.onApplicationEvent(deleteEvent);

			assertThat(deleteEvent.getEventType()).isEqualTo("DELETE");
		}

		@Test
		@DisplayName("Test CatalogoEventListener can be invoked multiple times")
		void test_catalogo_event_listener_multiple_invocations() {
			listener.onApplicationEvent(event);
			listener.onApplicationEvent(event);
			listener.onApplicationEvent(event);

			assertThat(event).isNotNull();
		}

		@Test
		@DisplayName("Test CatalogoEventListener with null catalog")
		void test_catalogo_event_listener_null_catalog() {
			CatalogoServiceEvent nullEvent = new CatalogoServiceEvent(
					new Object(), "CREATE", null);

			listener.onApplicationEvent(nullEvent);

			assertThat(nullEvent.getCatalogo()).isNull();
		}
	}

	@Nested
	@DisplayName("AbstractRestHandler Tests")
	class AbstractRestHandlerTests {

		private ConcreteRestHandler handler;

		@BeforeEach
		void setup() {
			handler = new ConcreteRestHandler();
		}

		@Test
		@DisplayName("Test AbstractRestHandler exception handler for HTTP400Exception")
		void test_abstract_rest_handler_http400_exception() {
			HTTP400Exception exception = new HTTP400Exception("Bad request");

			ErrorResponse response = handler.handleDataStoreException(exception, null, null);

			assertThat(response).isNotNull();
			assertThat(response.errorDetail).isEqualTo("Bad request");
		}

		@Test
		@DisplayName("Test AbstractRestHandler exception handler for HTTP401Exception")
		void test_abstract_rest_handler_http401_exception() {
			HTTP401Exception exception = new HTTP401Exception("Unauthorized");

			ErrorResponse response = handler.handleUnauthorizedException(
					exception, null, null);

			assertThat(response).isNotNull();
			assertThat(response.errorDetail).isEqualTo("Unauthorized");
		}

		@Test
		@DisplayName("Test AbstractRestHandler exception handler for HTTP403Exception")
		void test_abstract_rest_handler_http403_exception() {
			HTTP403Exception exception = new HTTP403Exception("Forbidden");

			ErrorResponse response = handler.handleForbiddenException(
					exception, null, null);

			assertThat(response).isNotNull();
			assertThat(response.errorDetail).isEqualTo("Forbidden");
		}

		@Test
		@DisplayName("Test AbstractRestHandler exception handler for HTTP404Exception")
		void test_abstract_rest_handler_http404_exception() {
			HTTP404Exception exception = new HTTP404Exception("Not found");

			ErrorResponse response = handler.handleResourceNotFoundException(
					exception, null, null);

			assertThat(response).isNotNull();
			assertThat(response.errorDetail).isEqualTo("Not found");
		}

		@Test
		@DisplayName("Test AbstractRestHandler exception handler for HTTP500Exception")
		void test_abstract_rest_handler_http500_exception() {
			HTTP500Exception exception = new HTTP500Exception("Server error");

			ErrorResponse response = handler.handleResourceNotFoundException(
					exception, null, null);

			assertThat(response).isNotNull();
			assertThat(response.errorDetail).isEqualTo("Server error");
		}

		@Test
		@DisplayName("Test AbstractRestHandler setApplicationEventPublisher")
		void test_abstract_rest_handler_set_event_publisher() {
			ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

			handler.setApplicationEventPublisher(publisher);

			assertThat(handler.eventPublisher).isEqualTo(publisher);
		}

		@Test
		@DisplayName("Test AbstractRestHandler checkResourceFound with empty optional throws exception")
		void test_abstract_rest_handler_check_resource_found_empty() {
			org.junit.jupiter.api.Assertions.assertThrows(HTTP404Exception.class, () -> {
				AbstractRestHandler.checkResourceFound(java.util.Optional.empty());
			});
		}

		@Test
		@DisplayName("Test AbstractRestHandler checkResourceFound with present optional")
		void test_abstract_rest_handler_check_resource_found_present() {
			String resource = "Test Resource";

			java.util.Optional<String> result = AbstractRestHandler.checkResourceFound(
					java.util.Optional.of(resource));

			assertThat(result).isPresent();
			assertThat(result.get()).isEqualTo(resource);
		}

		@Test
		@DisplayName("Test AbstractRestHandler default page constants")
		void test_abstract_rest_handler_constants() {
			assertThat(AbstractRestHandler.DEFAULT_PAGE_SIZE).isEqualTo("10");
			assertThat(AbstractRestHandler.DEFAULT_PAGE_NUM).isEqualTo("0");
			assertThat(AbstractRestHandler.DEFAULT_PAGE_ORDER).isEqualTo("ASC");
		}
	}

	static class ConcreteRestHandler extends AbstractRestHandler {
	}
}
