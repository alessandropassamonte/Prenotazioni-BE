package it.company.deskbooking.controller;

import it.company.deskbooking.dto.*;
import it.company.deskbooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "API per la gestione degli utenti")
public class UserController {

    private final UserService userService;

    /**
     * Ottiene tutti gli utenti (solo Admin e Manager)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lista utenti", description = "Ottiene la lista di tutti gli utenti")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Richiesta lista utenti");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Ottiene utente per ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Dettagli utente", description = "Ottiene i dettagli di un utente specifico")
    public ResponseEntity<UserDetailDTO> getUserById(@PathVariable Long id) {
        log.info("Richiesta dettagli utente ID: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Ottiene utente per email
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cerca utente per email", description = "Ottiene un utente tramite email")
    public ResponseEntity<UserDetailDTO> getUserByEmail(@PathVariable String email) {
        log.info("Richiesta utente per email: {}", email);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Aggiorna utente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    @Operation(summary = "Aggiorna utente", description = "Aggiorna i dati di un utente")
    public ResponseEntity<UserDetailDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("Richiesta aggiornamento utente ID: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Cambia password
     */
    @PostMapping("/{id}/change-password")
    @PreAuthorize("@userSecurity.isOwner(#id)")
    @Operation(summary = "Cambia password", description = "Cambia la password di un utente")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.info("Richiesta cambio password per utente ID: {}", id);
        userService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Elimina utente (solo Admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimina utente", description = "Elimina un utente dal sistema")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Richiesta eliminazione utente ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
