package com.sosa.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP Exception Tests")
class ExceptionTests {

	private static final String TEST_MESSAGE = "Test error message";
	private static final Throwable TEST_CAUSE = new RuntimeException("Test cause");

	@Nested
	@DisplayName("HTTP 400 Bad Request Exception Tests")
	class HTTP400ExceptionTests {

		@Test
		@DisplayName("Test default constructor")
		void test_http400_default_constructor() {
			HTTP400Exception exception = new HTTP400Exception();
			assertThat(exception).isNotNull();
			assertThat(exception).isInstanceOf(RuntimeException.class);
		}

		@Test
		@DisplayName("Test constructor with message")
		void test_http400_message_constructor() {
			HTTP400Exception exception = new HTTP400Exception(TEST_MESSAGE);
			assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
		}

		@Test
		@DisplayName("Test constructor with message and cause")
		void test_http400_message_and_cause_constructor() {
			HTTP400Exception exception = new HTTP400Exception(TEST_MESSAGE, TEST_CAUSE);
			assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
			assertThat(exception.getCause()).isEqualTo(TEST_CAUSE);
		}

		@Test
		@DisplayName("Test constructor with cause")
		void test_http400_cause_constructor() {
			HTTP400Exception exception = new HTTP400Exception(TEST_CAUSE);
			assertThat(exception.getCause()).isEqualTo(TEST_CAUSE);
		}

		@Test
		@DisplayName("Test throwing HTTP400Exception")
		void test_throwing_http400_exception() {
			HTTP400Exception thrown = assertThrows(HTTP400Exception.class, () -> {
				throw new HTTP400Exception(TEST_MESSAGE);
			});
			assertThat(thrown.getMessage()).isEqualTo(TEST_MESSAGE);
		}
	}

	@Nested
	@DisplayName("HTTP 401 Unauthorized Exception Tests")
	class HTTP401ExceptionTests {

		@Test
		@DisplayName("Test default constructor")
		void test_http401_default_constructor() {
			HTTP401Exception exception = new HTTP401Exception();
			assertThat(exception).isNotNull();
			assertThat(exception).isInstanceOf(RuntimeException.class);
		}

		@Test
		@DisplayName("Test constructor with message")
		void test_http401_message_constructor() {
			HTTP401Exception exception = new HTTP401Exception(TEST_MESSAGE);
			assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
		}

		@Test
		@DisplayName("Test constructor with message and cause")
		void test_http401_message_and_cause_constructor() {
			HTTP401Exception exception = new HTTP401Exception(TEST_MESSAGE, TEST_CAUSE);
			assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
			assertThat(exception.getCause()).isEqualTo(TEST_CAUSE);
		}

		@Test
		@DisplayName("Test constructor with cause")
		void test_http401_cause_constructor() {
			HTTP401Exception exception = new HTTP401Exception(TEST_CAUSE);
			assertThat(exception.getCause()).isEqualTo(TEST_CAUSE);
		}

		@Test
		@DisplayName("Test throwing HTTP401Exception")
		void test_throwing_http401_exception() {
			HTTP401Exception thrown = assertThrows(HTTP401Exception.class, () -> {
				throw new HTTP401Exception(TEST_MESSAGE);
			});
			assertThat(thrown.getMessage()).isEqualTo(TEST_MESSAGE);
		}
	}

	@Nested
	@DisplayName("HTTP 403 Forbidden Exception Tests")
	class HTTP403ExceptionTests {

		@Test
		@DisplayName("Test default constructor")
		void test_http403_default_constructor() {
			HTTP403Exception exception = new HTTP403Exception();
			assertThat(exception).isNotNull();
			assertThat(exception).isInstanceOf(RuntimeException.class);
		}

		@Test
		@DisplayName("Test constructor with message")
		void test_http403_message_constructor() {
			HTTP403Exception exception = new HTTP403Exception(TEST_MESSAGE);
			assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
		}

		@Test
		@DisplayName("Test constructor with message and cause")
		void test_http403_message_and_cause_constructor() {
			HTTP403Exception exception = new HTTP403Exception(TEST_MESSAGE, TEST_CAUSE);
			assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
			assertThat(exception.getCause()).isEqualTo(TEST_CAUSE);
		}

		@Test
		@DisplayName("Test constructor with cause")
		void test_http403_cause_constructor() {
			HTTP403Exception exception = new HTTP403Exception(TEST_CAUSE);
			assertThat(exception.getCause()).isEqualTo(TEST_CAUSE);
		}

		@Test
		@DisplayName("Test throwing HTTP403Exception")
		void test_throwing_http403_exception() {
			HTTP403Exception thrown = assertThrows(HTTP403Exception.class, () -> {
				throw new HTTP403Exception(TEST_MESSAGE);
			});
			assertThat(thrown.getMessage()).isEqualTo(TEST_MESSAGE);
		}
	}

	@Nested
	@DisplayName("HTTP 404 Not Found Exception Tests")
	class HTTP404ExceptionTests {

		@Test
		@DisplayName("Test default constructor")
		void test_http404_default_constructor() {
			HTTP404Exception exception = new HTTP404Exception();
			assertThat(exception).isNotNull();
			assertThat(exception).isInstanceOf(RuntimeException.class);
		}

		@Test
		@DisplayName("Test constructor with message")
		void test_http404_message_constructor() {
			HTTP404Exception exception = new HTTP404Exception(TEST_MESSAGE);
			assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
		}

		@Test
		@DisplayName("Test constructor with message and cause")
		void test_http404_message_and_cause_constructor() {
			HTTP404Exception exception = new HTTP404Exception(TEST_MESSAGE, TEST_CAUSE);
			assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
			assertThat(exception.getCause()).isEqualTo(TEST_CAUSE);
		}

		@Test
		@DisplayName("Test constructor with cause")
		void test_http404_cause_constructor() {
			HTTP404Exception exception = new HTTP404Exception(TEST_CAUSE);
			assertThat(exception.getCause()).isEqualTo(TEST_CAUSE);
		}

		@Test
		@DisplayName("Test throwing HTTP404Exception")
		void test_throwing_http404_exception() {
			HTTP404Exception thrown = assertThrows(HTTP404Exception.class, () -> {
				throw new HTTP404Exception(TEST_MESSAGE);
			});
			assertThat(thrown.getMessage()).isEqualTo(TEST_MESSAGE);
		}
	}

	@Nested
	@DisplayName("HTTP 500 Server Error Exception Tests")
	class HTTP500ExceptionTests {

		@Test
		@DisplayName("Test default constructor")
		void test_http500_default_constructor() {
			HTTP500Exception exception = new HTTP500Exception();
			assertThat(exception).isNotNull();
			assertThat(exception).isInstanceOf(RuntimeException.class);
		}

		@Test
		@DisplayName("Test constructor with message")
		void test_http500_message_constructor() {
			HTTP500Exception exception = new HTTP500Exception(TEST_MESSAGE);
			assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
		}

		@Test
		@DisplayName("Test constructor with message and cause")
		void test_http500_message_and_cause_constructor() {
			HTTP500Exception exception = new HTTP500Exception(TEST_MESSAGE, TEST_CAUSE);
			assertThat(exception.getMessage()).isEqualTo(TEST_MESSAGE);
			assertThat(exception.getCause()).isEqualTo(TEST_CAUSE);
		}

		@Test
		@DisplayName("Test constructor with cause")
		void test_http500_cause_constructor() {
			HTTP500Exception exception = new HTTP500Exception(TEST_CAUSE);
			assertThat(exception.getCause()).isEqualTo(TEST_CAUSE);
		}

		@Test
		@DisplayName("Test throwing HTTP500Exception")
		void test_throwing_http500_exception() {
			HTTP500Exception thrown = assertThrows(HTTP500Exception.class, () -> {
				throw new HTTP500Exception(TEST_MESSAGE);
			});
			assertThat(thrown.getMessage()).isEqualTo(TEST_MESSAGE);
		}
	}
}
