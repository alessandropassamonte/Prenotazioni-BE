package it.company.deskbooking.controller;

import it.company.deskbooking.dto.*;
import it.company.deskbooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Bookings", description = "API per la gestione delle prenotazioni")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lista tutte le prenotazioni", description = "Ottiene tutte le prenotazioni (solo Admin/Manager)")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<BookingDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @bookingSecurity.isOwner(#id)")
    @Operation(summary = "Dettagli prenotazione", description = "Ottiene i dettagli di una prenotazione")
    public ResponseEntity<BookingDetailDTO> getBookingById(@PathVariable Long id) {
        BookingDetailDTO booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isOwner(#userId)")
    @Operation(summary = "Prenotazioni utente", description = "Ottiene tutte le prenotazioni di un utente")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        List<BookingDTO> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user/{userId}/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isOwner(#userId)")
    @Operation(summary = "Prenotazioni future utente", description = "Ottiene le prenotazioni future di un utente")
    public ResponseEntity<List<BookingDTO>> getUserUpcomingBookings(@PathVariable Long userId) {
        List<BookingDTO> bookings = bookingService.getUserUpcomingBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Prenotazioni per data", description = "Ottiene tutte le prenotazioni per una data specifica")
    public ResponseEntity<List<BookingDTO>> getBookingsForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BookingDTO> bookings = bookingService.getBookingsForDate(date);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/date/{date}/floor/{floorId}")
    @Operation(summary = "Prenotazioni per data e piano", description = "Ottiene le prenotazioni per data e piano specifici")
    public ResponseEntity<List<BookingDTO>> getBookingsForDateAndFloor(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable Long floorId) {
        List<BookingDTO> bookings = bookingService.getBookingsForDateAndFloor(date, floorId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isOwner(#userId)")
    @Operation(summary = "Crea prenotazione", description = "Crea una nuova prenotazione per un utente")
    public ResponseEntity<BookingDTO> createBooking(
            @PathVariable Long userId,
            @Valid @RequestBody CreateBookingRequest request) {
        log.info("Creazione prenotazione per utente: {}", userId);
        BookingDTO booking = bookingService.createBooking(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @bookingSecurity.isOwner(#id)")
    @Operation(summary = "Aggiorna prenotazione", description = "Aggiorna una prenotazione esistente")
    public ResponseEntity<BookingDTO> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingRequest request) {
        log.info("Aggiornamento prenotazione: {}", id);
        BookingDTO booking = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @bookingSecurity.isOwner(#id)")
    @Operation(summary = "Cancella prenotazione", description = "Cancella una prenotazione (solo proprietario o admin)")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @RequestBody(required = false) CancelBookingRequest request) {
        log.info("Cancellazione prenotazione: {}", id);
        if (request == null) {
            request = CancelBookingRequest.builder().build();
        }
        bookingService.cancelBooking(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/check-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @bookingSecurity.isOwner(#id)")
    @Operation(summary = "Check-in", description = "Effettua il check-in per una prenotazione")
    public ResponseEntity<BookingDTO> checkIn(@PathVariable Long id) {
        log.info("Check-in prenotazione: {}", id);
        BookingDTO booking = bookingService.checkIn(id);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{id}/check-out")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @bookingSecurity.isOwner(#id)")
    @Operation(summary = "Check-out", description = "Effettua il check-out per una prenotazione")
    public ResponseEntity<BookingDTO> checkOut(@PathVariable Long id) {
        log.info("Check-out prenotazione: {}", id);
        BookingDTO booking = bookingService.checkOut(id);
        return ResponseEntity.ok(booking);
    }
}