package it.company.deskbooking.controller;

import it.company.deskbooking.dto.CreateUserRequest;
import it.company.deskbooking.dto.LoginRequest;
import it.company.deskbooking.dto.LoginResponse;
import it.company.deskbooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API per autenticazione e registrazione")
public class AuthController {

    private final UserService userService;

    /**
     * Login utente
     */
    @PostMapping("/login")
    @Operation(summary = "Login utente", description = "Effettua il login e restituisce il token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Richiesta login per: {}", request.getEmail());
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Registrazione nuovo utente
     */
    @PostMapping("/register")
    @Operation(summary = "Registrazione utente", description = "Registra un nuovo utente e restituisce il token JWT")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody CreateUserRequest request) {
        log.info("Richiesta registrazione per: {}", request.getEmail());
        LoginResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Verifica validità token (opzionale - per debug)
     */
    @GetMapping("/verify")
    @Operation(summary = "Verifica token", description = "Verifica se il token JWT è ancora valido")
    public ResponseEntity<String> verifyToken() {
        return ResponseEntity.ok("Token valido");
    }
}
