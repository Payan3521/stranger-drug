package com.gateway.gateway.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LoggingService {
    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public void logInfo(String message, Object... args) {
        logger.info("[{}] {}", LocalDateTime.now().format(formatter), String.format(message, args));
    }

    public void logError(String message, Throwable error) {
        logger.error("[{}] {} - Error: {}", LocalDateTime.now().format(formatter), message, error.getMessage(), error);
    }

    public void logError(String message, Object... args) {
        logger.error("[{}] {}", LocalDateTime.now().format(formatter), String.format(message, args));
    }

    public void logWarning(String message, Object... args) {
        logger.warn("[{}] {}", LocalDateTime.now().format(formatter), String.format(message, args));
    }

    public void logDebug(String message, Object... args) {
        logger.debug("[{}] {}", LocalDateTime.now().format(formatter), String.format(message, args));
    }

    public void logTrace(String message, Object... args) {
        logger.trace("[{}] {}", LocalDateTime.now().format(formatter), String.format(message, args));
    }

    public void logSecurity(String message, Object... args) {
        logger.info("[{}] [SECURITY] {}", LocalDateTime.now().format(formatter), String.format(message, args));
    }

    public void logSecurityWarning(String message, Object... args) {
        logger.warn("[{}] [SECURITY-WARNING] {}", LocalDateTime.now().format(formatter), String.format(message, args));
    }

    public void logSecurityError(String message, Object... args) {
        logger.error("[{}] [SECURITY-ERROR] {}", LocalDateTime.now().format(formatter), String.format(message, args));
    }

    public void logGateway(String message, Object... args) {
        logger.info("[{}] [GATEWAY] {}", LocalDateTime.now().format(formatter), String.format(message, args));
    }

    public void logGatewayDebug(String message, Object... args) {
        logger.debug("[{}] [GATEWAY-DEBUG] {}", LocalDateTime.now().format(formatter), String.format(message, args));
    }
}