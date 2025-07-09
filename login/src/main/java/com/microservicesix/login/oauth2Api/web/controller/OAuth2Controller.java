package com.microservicesix.login.oauth2Api.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microservicesix.login.oauth2Api.application.service.OAuth2Service;
import com.microservicesix.login.oauth2Api.web.dto.AuthorizationRequest;
import com.microservicesix.login.oauth2Api.web.dto.TokenRequest;
import com.microservicesix.login.oauth2Api.web.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
@Tag(name = "OAuth2", description = "API para OAuth2.0 Authorization Server")
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;

    @Operation(summary = "Authorization Endpoint", description = "Endpoint de autorización OAuth2.0")
    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestParam("response_type") String responseType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request) {
        

        AuthorizationRequest authRequest = AuthorizationRequest.builder()
                .responseType(responseType)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .scope(scope)
                .state(state)
                .build();

        // En un escenario real, aquí redirigirías a una página de login/consentimiento
        // Por ahora, simulamos que el usuario ya está autenticado
        String authorizationCode = oauth2Service.generateAuthorizationCode(authRequest);
        
        String redirectUrl = redirectUri + "?code=" + authorizationCode;
        if (state != null) {
            redirectUrl += "&state=" + state;
        }

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    @Operation(summary = "Token Endpoint", description = "Intercambio de authorization code por tokens")
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(@Valid @RequestBody TokenRequest tokenRequest) {
        
        TokenResponse tokenResponse = oauth2Service.exchangeCodeForTokens(tokenRequest);
        
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "Token Info", description = "Información sobre un token")
    @PostMapping("/introspect")
    public ResponseEntity<?> introspect(@RequestParam("token") String token) {
        
        boolean isActive = oauth2Service.introspectToken(token);
        
        return ResponseEntity.ok(java.util.Map.of(
            "active", isActive,
            "token_type", "Bearer",
            "scope", "read write"
        ));
    }
}
