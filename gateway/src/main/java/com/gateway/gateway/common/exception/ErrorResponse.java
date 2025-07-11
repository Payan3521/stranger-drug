package com.gateway.gateway.common.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, Object> details;
    
    public static ErrorResponse of(int status, String error, String message) {
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .error(error)
            .message(message)
            .build();
    }
    
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .error(error)
            .message(message)
            .path(path)
            .build();
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", timestamp);
        map.put("status", status);
        map.put("error", error);
        map.put("message", message);
        
        if (path != null) {
            map.put("path", path);
        }
        
        if (details != null && !details.isEmpty()) {
            map.putAll(details);
        }
        
        return map;
    }
    
    public ErrorResponse addDetail(String key, Object value) {
        if (details == null) {
            details = new HashMap<>();
        }
        details.put(key, value);
        return this;
    }
} 