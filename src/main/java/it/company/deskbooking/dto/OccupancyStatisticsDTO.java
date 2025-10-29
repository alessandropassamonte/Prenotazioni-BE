package it.company.deskbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO per le statistiche di occupazione delle postazioni
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyStatisticsDTO {

    // Statistiche globali del periodo
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalWorkingDays; // Giorni lavorativi nel periodo
    private Integer totalDesks; // Totale postazioni disponibili
    
    // Statistiche medie del periodo
    private BigDecimal averageOccupancyRate; // Percentuale media occupazione
    private Long averageOccupiedDesks; // Media postazioni occupate per giorno
    private Long averageFreeDesks; // Media postazioni libere per giorno
    
    // Picchi e minimi
    private DayOccupancyDTO mostOccupiedDay; // Giorno con più occupazione
    private DayOccupancyDTO leastOccupiedDay; // Giorno con meno occupazione
    
    // Statistiche giornaliere dettagliate
    private List<DayOccupancyDTO> dailyOccupancy;
    
    // Statistiche per piano
    private List<FloorOccupancyStatsDTO> floorStatistics;
    
    /**
     * DTO per l'occupazione di un singolo giorno
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayOccupancyDTO {
        private LocalDate date;
        private String dayOfWeek;
        private Long occupiedDesks;
        private Long freeDesks;
        private Integer totalDesks;
        private BigDecimal occupancyRate;
        private Boolean isHoliday;
        private String holidayName;
    }
    
    /**
     * DTO per statistiche per piano
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FloorOccupancyStatsDTO {
        private Long floorId;
        private String floorName;
        private Integer floorNumber;
        private Integer totalDesks;
        private BigDecimal averageOccupancyRate;
        private Long totalBookings; // Totale prenotazioni nel periodo
    }
}
