package it.company.deskbooking.controller;

import it.company.deskbooking.dto.OccupancyStatisticsDTO;
import it.company.deskbooking.service.OccupancyStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Occupancy Statistics", description = "API per statistiche e report di occupazione postazioni")
public class OccupancyStatisticsController {

    private final OccupancyStatisticsService statisticsService;

    @GetMapping("/occupancy")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Statistiche occupazione per periodo",
            description = "Ottiene statistiche dettagliate sull'occupazione delle postazioni per un periodo specificato. " +
                    "Include dati giornalieri, medie, picchi e statistiche per piano. Solo per Admin/Manager."
    )
    public ResponseEntity<OccupancyStatisticsDTO> getOccupancyStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Richiesta statistiche occupazione dal {} al {}", startDate, endDate);
        
        OccupancyStatisticsDTO statistics = statisticsService.getOccupancyStatistics(startDate, endDate);
        
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/occupancy/today")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Statistiche occupazione oggi",
            description = "Ottiene le statistiche di occupazione per il giorno corrente"
    )
    public ResponseEntity<OccupancyStatisticsDTO> getTodayOccupancyStatistics() {
        LocalDate today = LocalDate.now();
        log.info("Richiesta statistiche occupazione per oggi: {}", today);
        
        OccupancyStatisticsDTO statistics = statisticsService.getOccupancyStatistics(today, today);
        
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/occupancy/week")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Statistiche occupazione settimana corrente",
            description = "Ottiene le statistiche di occupazione per la settimana corrente (da lunedì a oggi)"
    )
    public ResponseEntity<OccupancyStatisticsDTO> getCurrentWeekOccupancyStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        
        log.info("Richiesta statistiche occupazione per settimana corrente: {} - {}", startOfWeek, today);
        
        OccupancyStatisticsDTO statistics = statisticsService.getOccupancyStatistics(startOfWeek, today);
        
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/occupancy/month")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Statistiche occupazione mese corrente",
            description = "Ottiene le statistiche di occupazione per il mese corrente"
    )
    public ResponseEntity<OccupancyStatisticsDTO> getCurrentMonthOccupancyStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        log.info("Richiesta statistiche occupazione per mese corrente: {} - {}", startOfMonth, today);
        
        OccupancyStatisticsDTO statistics = statisticsService.getOccupancyStatistics(startOfMonth, today);
        
        return ResponseEntity.ok(statistics);
    }
}
