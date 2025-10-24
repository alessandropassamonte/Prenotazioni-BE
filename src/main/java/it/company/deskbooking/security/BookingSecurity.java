package it.company.deskbooking.security;

import it.company.deskbooking.model.Booking;
import it.company.deskbooking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component("bookingSecurity")
@RequiredArgsConstructor
public class BookingSecurity {

    private final BookingRepository bookingRepository;

    /**
     * Verifica se l'utente corrente è il proprietario della prenotazione
     */
    public boolean isOwner(Long bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("Nessuna autenticazione trovata");
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            log.debug("Principal non è UserDetails");
            return false;
        }

        String username = ((UserDetails) principal).getUsername(); // email
        log.debug("Verifico se utente {} è proprietario della prenotazione {}", username, bookingId);

        // Carica la prenotazione e verifica l'ownership
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    boolean isOwner = booking.getUser().getEmail().equals(username);
                    log.debug("Utente {} {} proprietario della prenotazione {}",
                            username, isOwner ? "è" : "non è", bookingId);
                    return isOwner;
                })
                .orElse(false);
    }
}




