package it.company.deskbooking.controller;

import it.company.deskbooking.dto.CompanyHolidayDTO;
import it.company.deskbooking.dto.CreateCompanyHolidayRequest;
import it.company.deskbooking.dto.UpdateCompanyHolidayRequest;
import it.company.deskbooking.service.CompanyHolidayService;
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
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Company Holidays", description = "API per la gestione dei giorni non lavorativi")
public class CompanyHolidayController {

    private final CompanyHolidayService holidayService;

    @GetMapping
    @Operation(summary = "Lista tutte le festività", description = "Ottiene tutte le festività attive")
    public ResponseEntity<List<CompanyHolidayDTO>> getAllHolidays() {
        List<CompanyHolidayDTO> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(holidays);
    }

    @GetMapping("/range")
    @Operation(summary = "Festività in range", description = "Ottiene le festività in un range di date")
    public ResponseEntity<List<CompanyHolidayDTO>> getHolidaysBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CompanyHolidayDTO> holidays = holidayService.getHolidaysBetween(startDate, endDate);
        return ResponseEntity.ok(holidays);
    }

    @GetMapping("/year/{year}")
    @Operation(summary = "Festività per anno",
            description = "Ottiene le festività per un anno specifico, generando automaticamente quelle ricorrenti se mancanti")
    public ResponseEntity<List<CompanyHolidayDTO>> getHolidaysByYear(@PathVariable int year) {
        log.info("Richiesta festività per l'anno: {}", year);
        List<CompanyHolidayDTO> holidays = holidayService.getOrCreateHolidaysForYear(year);
        return ResponseEntity.ok(holidays);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Dettagli festività", description = "Ottiene i dettagli di una festività")
    public ResponseEntity<CompanyHolidayDTO> getHolidayById(@PathVariable Long id) {
        CompanyHolidayDTO holiday = holidayService.getHolidayById(id);
        return ResponseEntity.ok(holiday);
    }

    @GetMapping("/check/{date}")
    @Operation(summary = "Verifica festività", description = "Verifica se una data è festiva")
    public ResponseEntity<Boolean> isHoliday(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean isHoliday = holidayService.isHoliday(date);
        return ResponseEntity.ok(isHoliday);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Crea festività", description = "Crea una nuova festività (solo Admin/Manager)")
    public ResponseEntity<CompanyHolidayDTO> createHoliday(
            @Valid @RequestBody CreateCompanyHolidayRequest request) {
        log.info("Richiesta creazione festività: {}", request.getName());
        CompanyHolidayDTO holiday = holidayService.createHoliday(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(holiday);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Aggiorna festività", description = "Aggiorna una festività (solo Admin/Manager)")
    public ResponseEntity<CompanyHolidayDTO> updateHoliday(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCompanyHolidayRequest request) {
        log.info("Richiesta aggiornamento festività: {}", id);
        CompanyHolidayDTO holiday = holidayService.updateHoliday(id, request);
        return ResponseEntity.ok(holiday);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Elimina festività", description = "Disattiva una festività (solo Admin/Manager)")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        log.info("Richiesta eliminazione festività: {}", id);
        holidayService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }
}




