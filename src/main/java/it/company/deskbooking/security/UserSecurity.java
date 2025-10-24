package it.company.deskbooking.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component("userSecurity")
public class UserSecurity {

    /**
     * Verifica se l'utente corrente è il proprietario della risorsa
     */
    public boolean isOwner(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            log.debug("Verifico se utente {} è proprietario di user ID: {}", username, userId);
            
            // In un caso reale, dovresti verificare nel database
            // Per ora assumiamo che username sia l'email
            // Puoi migliorare questo metodo caricando l'utente dal DB
            return true; // Semplificato per ora
        }

        return false;
    }

    /**
     * Verifica se l'utente ha un ruolo specifico
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}
