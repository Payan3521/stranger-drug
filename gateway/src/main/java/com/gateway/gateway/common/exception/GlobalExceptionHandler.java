package com.gateway.gateway.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import com.gateway.gateway.common.logging.LoggingService;
import com.gateway.gateway.microserviceUsers.jwt.domain.exception.AccessErrorException;
import com.gateway.gateway.microserviceUsers.jwt.domain.exception.InvalidTokenException;
import com.gateway.gateway.microserviceUsers.jwt.domain.exception.NoAdminAccessException;
import com.gateway.gateway.microserviceUsers.jwt.domain.exception.NoTokenException;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.exception.TooManyRequestsException;
import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LoggingService loggingService;

    // ================== EXCEPCIONES DE AUTENTICACIÓN Y AUTORIZACIÓN ==================

    @ExceptionHandler(InvalidTokenException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleInvalidTokenException(InvalidTokenException ex) {
        loggingService.logSecurityWarning("GlobalExceptionHandler: Token inválido - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED.value(),
            "Token inválido",
            ex.getMessage()
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(AccessErrorException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleAccessErrorException(AccessErrorException ex) {
        loggingService.logSecurityWarning("GlobalExceptionHandler: Error de acceso - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED.value(),
            "Error de acceso",
            ex.getMessage()
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(NoTokenException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNoTokenException(NoTokenException ex) {
        loggingService.logSecurityWarning("GlobalExceptionHandler: Token requerido - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Token requerido",
            ex.getMessage()
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(NoAdminAccessException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNoAdminAccessException(NoAdminAccessException ex) {
        loggingService.logSecurityWarning("GlobalExceptionHandler: Acceso de administrador requerido - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            "Acceso de administrador requerido",
            ex.getMessage()
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.FORBIDDEN));
    }

    // ================== EXCEPCIONES DE RATE LIMITING ==================

    @ExceptionHandler(TooManyRequestsException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleTooManyRequestsException(TooManyRequestsException ex) {
        loggingService.logSecurityWarning("GlobalExceptionHandler: Rate limit excedido - Usuario: {}, Endpoint: {}, Error: {}", 
            ex.getUserIdentifier(), ex.getEndpoint(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.TOO_MANY_REQUESTS.value(),
            "Too Many Requests",
            ex.getMessage()
        );
        
        if (ex.getUserIdentifier() != null) {
            errorResponse.addDetail("userIdentifier", ex.getUserIdentifier());
        }
        
        if (ex.getEndpoint() != null) {
            errorResponse.addDetail("endpoint", ex.getEndpoint());
            errorResponse.addDetail("currentCount", ex.getCurrentCount());
            errorResponse.addDetail("maxRequests", ex.getMaxRequests());
        }
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.TOO_MANY_REQUESTS));
    }

    // ================== EXCEPCIONES DE CONEXIÓN Y COMUNICACIÓN ==================

    @ExceptionHandler(ResourceAccessException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleResourceAccessException(ResourceAccessException ex) {
        loggingService.logError("GlobalExceptionHandler: Microservicio no disponible - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "Microservicio no disponible",
            "No se pudo conectar con el microservicio solicitado"
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.SERVICE_UNAVAILABLE));
    }

    @ExceptionHandler(ConnectException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleConnectException(ConnectException ex) {
        loggingService.logError("GlobalExceptionHandler: Error de conexión - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "Error de conexión",
            "No se pudo establecer conexión con el microservicio"
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.SERVICE_UNAVAILABLE));
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleSocketTimeoutException(SocketTimeoutException ex) {
        loggingService.logError("GlobalExceptionHandler: Timeout de conexión - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.REQUEST_TIMEOUT.value(),
            "Timeout de conexión",
            "La conexión con el microservicio ha expirado"
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.REQUEST_TIMEOUT));
    }

    @ExceptionHandler(TimeoutException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleTimeoutException(TimeoutException ex) {
        loggingService.logError("GlobalExceptionHandler: Timeout de solicitud - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.REQUEST_TIMEOUT.value(),
            "Timeout de solicitud",
            "La solicitud ha excedido el tiempo límite"
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.REQUEST_TIMEOUT));
    }

    // ================== EXCEPCIONES DE CLIENTE HTTP ==================

    @ExceptionHandler(HttpClientErrorException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleHttpClientErrorException(HttpClientErrorException ex) {
        loggingService.logError("GlobalExceptionHandler: Error del cliente HTTP - Status: {}, Error: {}", 
            ex.getStatusCode(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            ex.getStatusCode().value(),
            "Error del cliente",
            "Error en la solicitud al microservicio: " + ex.getStatusText()
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), ex.getStatusCode()));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleHttpServerErrorException(HttpServerErrorException ex) {
        loggingService.logError("GlobalExceptionHandler: Error del servidor HTTP - Status: {}, Error: {}", 
            ex.getStatusCode(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            ex.getStatusCode().value(),
            "Error del servidor",
            "Error interno en el microservicio: " + ex.getStatusText()
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), ex.getStatusCode()));
    }

    @ExceptionHandler(RestClientException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleRestClientException(RestClientException ex) {
        loggingService.logError("GlobalExceptionHandler: Error de comunicación REST - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.BAD_GATEWAY.value(),
            "Error de comunicación",
            "Error al comunicarse con el microservicio"
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.BAD_GATEWAY));
    }

    // ================== EXCEPCIONES DE WEBFLUX ==================

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleWebClientResponseException(WebClientResponseException ex) {
        loggingService.logError("GlobalExceptionHandler: Error de WebClient - Status: {}, Error: {}", 
            ex.getStatusCode(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            ex.getStatusCode().value(),
            "Error de comunicación",
            "Error al comunicarse con el microservicio: " + ex.getStatusText()
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), ex.getStatusCode()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleResponseStatusException(ResponseStatusException ex) {
        loggingService.logError("GlobalExceptionHandler: Error de respuesta - Status: {}, Error: {}", 
            ex.getStatusCode(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            ex.getStatusCode().value(),
            "Error de respuesta",
            ex.getMessage()
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), ex.getStatusCode()));
    }

    // ================== EXCEPCIONES GENERALES ==================

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleRuntimeException(RuntimeException ex) {
        loggingService.logError("GlobalExceptionHandler: Error de ejecución en tiempo de ejecución - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error de ejecución en tiempo de ejecución",
            ex.getMessage()
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        loggingService.logError("GlobalExceptionHandler: Error inesperado - Error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor",
            "Ha ocurrido un error inesperado en el gateway"
        );
        
        return Mono.just(new ResponseEntity<>(errorResponse.toMap(), HttpStatus.INTERNAL_SERVER_ERROR));
    }
} 