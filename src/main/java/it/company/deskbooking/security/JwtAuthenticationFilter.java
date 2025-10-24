package it.company.deskbooking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Se non c'è il token o non inizia con "Bearer ", passa al prossimo filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Estrae il token
        jwt = authHeader.substring(7);
        
        try {
            // Estrae l'email dal token
            userEmail = jwtUtil.extractUsername(jwt);

            // Se c'è l'email e l'utente non è già autenticato
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Carica i dettagli dell'utente
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Valida il token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    
                    // Crea l'authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Imposta l'autenticazione nel SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("Utente autenticato: {}", userEmail);
                }
            }
        } catch (Exception e) {
            log.error("Errore durante la validazione del JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
