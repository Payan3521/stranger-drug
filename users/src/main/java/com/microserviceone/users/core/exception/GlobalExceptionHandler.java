package com.microserviceone.users.core.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.microserviceone.users.registrationApi.application.exception.UserAlreadyRegisteredException;
import com.microserviceone.users.registrationApi.application.exception.UserNotFoundException;
import com.microserviceone.users.registrationApi.application.exception.UserPrerequisitesNotMetException;
import com.microserviceone.users.registrationApi.domain.exception.AgeIllegalException;
import com.microserviceone.users.termsAndConditionsApi.application.exception.UserAlreadyAcceptedException;
import com.microserviceone.users.termsAndConditionsApi.application.exception.TermNotActiveOrNotFoundException;
import com.microserviceone.users.termsAndConditionsApi.application.exception.TermsNotAcceptedException;
import com.microserviceone.users.verificationCodeApi.application.exception.CodeIsNotValidException;
import com.microserviceone.users.verificationCodeApi.application.exception.CodeNotFoundException;
import com.microserviceone.users.verificationCodeApi.application.exception.EmailNotVerifiedException;
import com.microserviceone.users.core.exception.jwt.MissingInternalJwtException;
import com.microserviceone.users.core.exception.jwt.InvalidInternalJwtIssuerException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ================== EXCEPCIONES DE REGISTRO ==================

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Usuario ya registrado");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Usuario no encontrado");
        body.put("message", ex.getMessage());
        body.put("userId", ex.getId());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AgeIllegalException.class)
    public ResponseEntity<Map<String, Object>> handleAgeIllegalException(AgeIllegalException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Edad no permitida");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserPrerequisitesNotMetException.class)
    public ResponseEntity<Map<String, Object>> handleUserPrerequisitesNotMetException(UserPrerequisitesNotMetException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Prerrequisitos de usuario no cumplidos");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ================== EXCEPCIONES DE TÉRMINOS Y CONDICIONES ==================

    @ExceptionHandler(UserAlreadyAcceptedException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyAcceptedException(UserAlreadyAcceptedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "El usuario ya aceptó este término");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TermNotActiveOrNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTermNotActiveOrNotFoundException(TermNotActiveOrNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Término no activo o no encontrado");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TermsNotAcceptedException.class)
    public ResponseEntity<Map<String, Object>> handleTermsNotAcceptedException(TermsNotAcceptedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Términos no aceptados");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ================== EXCEPCIONES DE VERIFICACIÓN ==================

    @ExceptionHandler(CodeIsNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleCodeIsNotValidException(CodeIsNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Código no válido o expirado");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CodeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCodeNotFoundException(CodeNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Código no encontrado");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String, Object>> handleEmailNotVerifiedException(EmailNotVerifiedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Email no verificado");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ================== EXCEPCIONES DE CORREO ==================

    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String, Object>> handleMailException(MailException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", "Error al enviar el correo electrónico");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // ================== EXCEPCIONES GENERALES ==================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error de ejecución en tiempo de ejecución");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error interno del servidor");
        body.put("message", "Ha ocurrido un error inesperado");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validación fallida");
        body.put("message", "Los datos proporcionados no son válidos");
        
        // Agregar detalles de los errores de validación
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage()));
        body.put("fieldErrors", fieldErrors);
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "JSON malformado");
        body.put("message", "El cuerpo de la solicitud no contiene un JSON válido");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        body.put("error", "Tipo de contenido no soportado");
        body.put("message", "Se esperaba Content-Type: application/json");
        return new ResponseEntity<>(body, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // ================== EXCEPCIONES JWT INTERNO ==================

    @ExceptionHandler(MissingInternalJwtException.class)
    public ResponseEntity<Map<String, Object>> handleMissingInternalJwtException(MissingInternalJwtException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Token interno no enviado");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidInternalJwtIssuerException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidInternalJwtIssuerException(InvalidInternalJwtIssuerException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Issuer del token interno inválido");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
}