package it.company.deskbooking.service;

import it.company.deskbooking.dto.OccupancyStatisticsDTO;
import it.company.deskbooking.dto.OccupancyStatisticsDTO.DayOccupancyDTO;
import it.company.deskbooking.dto.OccupancyStatisticsDTO.FloorOccupancyStatsDTO;
import it.company.deskbooking.model.CompanyHoliday;
import it.company.deskbooking.repository.BookingRepository;
import it.company.deskbooking.repository.CompanyHolidayRepository;
import it.company.deskbooking.repository.DeskRepository;
import it.company.deskbooking.repository.FloorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OccupancyStatisticsService {

    private final BookingRepository bookingRepository;
    private final DeskRepository deskRepository;
    private final FloorRepository floorRepository;
    private final CompanyHolidayRepository holidayRepository;

    /**
     * Calcola le statistiche di occupazione per un periodo specificato
     */
    public OccupancyStatisticsDTO getOccupancyStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("Calcolo statistiche occupazione dal {} al {}", startDate, endDate);

        // Validazione date
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La data di inizio deve essere precedente alla data di fine");
        }

        // Recupera festività nel periodo
        List<CompanyHoliday> holidays = holidayRepository.findByDateBetweenAndActiveTrue(startDate, endDate);
        Set<LocalDate> holidayDates = holidays.stream()
                .map(CompanyHoliday::getDate)
                .collect(Collectors.toSet());

        // Recupera totale postazioni attive
        Long totalDesks = deskRepository.countByActiveTrue();

        // Calcola statistiche giornaliere
        List<DayOccupancyDTO> dailyStats = calculateDailyOccupancy(startDate, endDate, holidayDates, holidays, totalDesks.intValue());

        // Filtra solo giorni lavorativi per calcoli statistici
        List<DayOccupancyDTO> workingDaysStats = dailyStats.stream()
                .filter(d -> !d.getIsHoliday() && !isWeekend(d.getDate()))
                .collect(Collectors.toList());

        // Calcola statistiche aggregate
        BigDecimal avgOccupancyRate = calculateAverageOccupancyRate(workingDaysStats);
        Long avgOccupiedDesks = calculateAverageOccupiedDesks(workingDaysStats);
        Long avgFreeDesks = totalDesks - avgOccupiedDesks;

        // Trova picchi e minimi
        DayOccupancyDTO mostOccupied = findMostOccupiedDay(workingDaysStats);
        DayOccupancyDTO leastOccupied = findLeastOccupiedDay(workingDaysStats);

        // Calcola statistiche per piano
        List<FloorOccupancyStatsDTO> floorStats = calculateFloorStatistics(startDate, endDate);

        return OccupancyStatisticsDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalWorkingDays(workingDaysStats.size())
                .totalDesks(totalDesks.intValue())
                .averageOccupancyRate(avgOccupancyRate)
                .averageOccupiedDesks(avgOccupiedDesks)
                .averageFreeDesks(avgFreeDesks)
                .mostOccupiedDay(mostOccupied)
                .leastOccupiedDay(leastOccupied)
                .dailyOccupancy(dailyStats)
                .floorStatistics(floorStats)
                .build();
    }

    /**
     * Calcola l'occupazione giornaliera per ogni giorno nel periodo
     */
    private List<DayOccupancyDTO> calculateDailyOccupancy(
            LocalDate startDate,
            LocalDate endDate,
            Set<LocalDate> holidayDates,
            List<CompanyHoliday> holidays,
            Integer totalDesks) {

        List<DayOccupancyDTO> dailyStats = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            // Verifica se è festività
            boolean isHoliday = holidayDates.contains(currentDate);
            String holidayName = null;
            
            if (isHoliday) {
                LocalDate finalCurrentDate = currentDate;
                holidayName = holidays.stream()
                        .filter(h -> h.getDate().equals(finalCurrentDate))
                        .findFirst()
                        .map(CompanyHoliday::getName)
                        .orElse(null);
            }

            // Conta postazioni occupate per questa data
            Long occupiedDesks = bookingRepository.countOccupiedDesksForDate(currentDate);
            Long freeDesks = totalDesks - occupiedDesks;

            // Calcola percentuale occupazione
            BigDecimal occupancyRate = totalDesks > 0
                    ? BigDecimal.valueOf(occupiedDesks)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalDesks), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            DayOccupancyDTO dayStats = DayOccupancyDTO.builder()
                    .date(currentDate)
                    .dayOfWeek(currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN))
                    .occupiedDesks(occupiedDesks)
                    .freeDesks(freeDesks)
                    .totalDesks(totalDesks)
                    .occupancyRate(occupancyRate)
                    .isHoliday(isHoliday || isWeekend(currentDate))
                    .holidayName(holidayName)
                    .build();

            dailyStats.add(dayStats);
            currentDate = currentDate.plusDays(1);
        }

        return dailyStats;
    }

    /**
     * Calcola la percentuale media di occupazione
     */
    private BigDecimal calculateAverageOccupancyRate(List<DayOccupancyDTO> workingDaysStats) {
        if (workingDaysStats.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = workingDaysStats.stream()
                .map(DayOccupancyDTO::getOccupancyRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(workingDaysStats.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcola la media di postazioni occupate
     */
    private Long calculateAverageOccupiedDesks(List<DayOccupancyDTO> workingDaysStats) {
        if (workingDaysStats.isEmpty()) {
            return 0L;
        }

        long sum = workingDaysStats.stream()
                .mapToLong(DayOccupancyDTO::getOccupiedDesks)
                .sum();

        return sum / workingDaysStats.size();
    }

    /**
     * Trova il giorno con maggiore occupazione
     */
    private DayOccupancyDTO findMostOccupiedDay(List<DayOccupancyDTO> workingDaysStats) {
        return workingDaysStats.stream()
                .max(Comparator.comparing(DayOccupancyDTO::getOccupiedDesks))
                .orElse(null);
    }

    /**
     * Trova il giorno con minore occupazione
     */
    private DayOccupancyDTO findLeastOccupiedDay(List<DayOccupancyDTO> workingDaysStats) {
        return workingDaysStats.stream()
                .min(Comparator.comparing(DayOccupancyDTO::getOccupiedDesks))
                .orElse(null);
    }

    /**
     * Calcola statistiche per ogni piano
     */
    private List<FloorOccupancyStatsDTO> calculateFloorStatistics(LocalDate startDate, LocalDate endDate) {
        return floorRepository.findByActiveTrue().stream()
                .map(floor -> {
                    Long totalDesks = deskRepository.countByFloorIdAndActiveTrue(floor.getId());
                    
                    // Calcola il totale prenotazioni per questo piano nel periodo
                    Long totalBookings = calculateTotalBookingsForFloor(floor.getId(), startDate, endDate);
                    
                    // Calcola giorni lavorativi nel periodo
                    long workingDays = countWorkingDays(startDate, endDate);
                    
                    // Calcola occupazione media
                    BigDecimal avgOccupancyRate = workingDays > 0 && totalDesks > 0
                            ? BigDecimal.valueOf(totalBookings)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(totalDesks * workingDays), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return FloorOccupancyStatsDTO.builder()
                            .floorId(floor.getId())
                            .floorName(floor.getName())
                            .floorNumber(floor.getFloorNumber())
                            .totalDesks(totalDesks.intValue())
                            .averageOccupancyRate(avgOccupancyRate)
                            .totalBookings(totalBookings)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcola il totale delle prenotazioni per un piano in un periodo
     */
    private Long calculateTotalBookingsForFloor(Long floorId, LocalDate startDate, LocalDate endDate) {
        // Conta le prenotazioni attive per ogni giorno e sommale
        long total = 0;
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            if (!isWeekend(currentDate) && !isHoliday(currentDate)) {
                total += bookingRepository.findActiveBookingsForDateAndFloor(currentDate, floorId).size();
            }
            currentDate = currentDate.plusDays(1);
        }
        
        return total;
    }

    /**
     * Conta i giorni lavorativi in un periodo
     */
    private long countWorkingDays(LocalDate startDate, LocalDate endDate) {
        long count = 0;
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            if (!isWeekend(currentDate) && !isHoliday(currentDate)) {
                count++;
            }
            currentDate = currentDate.plusDays(1);
        }
        
        return count;
    }

    /**
     * Verifica se una data è weekend
     */
    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    /**
     * Verifica se una data è festività
     */
    private boolean isHoliday(LocalDate date) {
        return holidayRepository.findByDateAndActiveTrue(date).isPresent();
    }
}
