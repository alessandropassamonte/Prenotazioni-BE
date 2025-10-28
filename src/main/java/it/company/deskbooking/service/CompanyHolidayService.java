package it.company.deskbooking.service;

import it.company.deskbooking.dto.CompanyHolidayDTO;
import it.company.deskbooking.dto.CreateCompanyHolidayRequest;
import it.company.deskbooking.dto.UpdateCompanyHolidayRequest;
import it.company.deskbooking.exception.ResourceNotFoundException;
import it.company.deskbooking.model.CompanyHoliday;
import it.company.deskbooking.repository.CompanyHolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyHolidayService {

    private final CompanyHolidayRepository holidayRepository;

    /**
     * Ottiene tutte le festività attive
     */
    public List<CompanyHolidayDTO> getAllHolidays() {
        return holidayRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Ottiene o crea le festività per un anno specifico
     * Se le festività ricorrenti non esistono per quell'anno, le crea automaticamente
     */
    @Transactional
    public List<CompanyHolidayDTO> getOrCreateHolidaysForYear(int year) {
        log.info("Ricerca festività per l'anno {} con generazione automatica ricorrenti", year);

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // Recupera le festività esistenti per l'anno
        List<CompanyHoliday> existingHolidays = holidayRepository.findByDateBetween(startDate, endDate);
        log.info("Trovate {} festività esistenti per l'anno {}", existingHolidays.size(), year);

        // Recupera tutte le festività ricorrenti
        List<CompanyHoliday> recurringHolidays = holidayRepository.findByRecurringTrueAndActiveTrue();
        log.info("Trovate {} festività ricorrenti totali", recurringHolidays.size());

        // Lista delle festività da creare
        Set<CompanyHoliday> holidaysToCreate = new HashSet<>();

        // Per ogni festività ricorrente, verifica se esiste già per l'anno corrente
        for (CompanyHoliday recurring : recurringHolidays) {
            // Crea la data per l'anno richiesto mantenendo giorno e mese della festività ricorrente
            LocalDate targetDate = LocalDate.of(year, recurring.getDate().getMonth(), recurring.getDate().getDayOfMonth());

            // Verifica se esiste già una festività per questa data
            Optional<CompanyHoliday> existingHoliday = holidayRepository.findByDateAndActiveTrue(targetDate);

            if (existingHoliday.isEmpty()) {
                CompanyHoliday newHoliday = CompanyHoliday.builder()
                        .date(targetDate)
                        .name(recurring.getName())
                        .description(recurring.getDescription())
                        .type(recurring.getType())
                        .recurring(true)
                        .active(true)
                        .build();
                holidaysToCreate.add(newHoliday);
            } else {
                log.debug("Festività '{}' già esistente per la data {}",
                        existingHoliday.get().getName(), targetDate);
            }

        }

        // Salva le nuove festività se ce ne sono
        if (!holidaysToCreate.isEmpty()) {
            log.info("Creazione di {} nuove festività ricorrenti per l'anno {}", holidaysToCreate.size(), year);
            List<CompanyHoliday> savedHolidays = holidayRepository.saveAll(holidaysToCreate);
            existingHolidays.addAll(savedHolidays);
        } else {
            log.info("Tutte le festività ricorrenti sono già presenti per l'anno {}", year);
        }

        // Ordina per data e converte in DTO
        return existingHolidays.stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Ottiene festività in un range di date
     */
    public List<CompanyHolidayDTO> getHolidaysBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Caricamento festività tra {} e {}", startDate, endDate);
        return holidayRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Ottiene festività per ID
     */
    public CompanyHolidayDTO getHolidayById(Long id) {
        CompanyHoliday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Festività non trovata con id: " + id));
        return convertToDTO(holiday);
    }

    /**
     * Verifica se una data è festivo
     */
    public boolean isHoliday(LocalDate date) {
        return holidayRepository.isHoliday(date);
    }

    /**
     * Crea una nuova festività
     */
    @Transactional
    public CompanyHolidayDTO createHoliday(CreateCompanyHolidayRequest request) {
        log.info("Creazione nuova festività: {} - {}", request.getDate(), request.getName());

        // Verifica se esiste già una festività per questa data
        if (holidayRepository.findByDateAndActiveTrue(request.getDate()).isPresent()) {
            throw new IllegalArgumentException("Esiste già una festività per la data: " + request.getDate());
        }

        CompanyHoliday holiday = CompanyHoliday.builder()
                .date(request.getDate())
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .recurring(request.getRecurring())
                .active(true)
                .build();

        CompanyHoliday savedHoliday = holidayRepository.save(holiday);
        log.info("Festività creata con successo: {}", savedHoliday.getId());

        return convertToDTO(savedHoliday);
    }

    /**
     * Aggiorna una festività
     */
    @Transactional
    public CompanyHolidayDTO updateHoliday(Long id, UpdateCompanyHolidayRequest request) {
        log.info("Aggiornamento festività: {}", id);

        CompanyHoliday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Festività non trovata con id: " + id));

        if (request.getDate() != null) {
            holiday.setDate(request.getDate());
        }
        if (request.getName() != null) {
            holiday.setName(request.getName());
        }
        if (request.getDescription() != null) {
            holiday.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            holiday.setType(request.getType());
        }
        if (request.getRecurring() != null) {
            holiday.setRecurring(request.getRecurring());
        }
        if (request.getActive() != null) {
            holiday.setActive(request.getActive());
        }

        CompanyHoliday updatedHoliday = holidayRepository.save(holiday);
        log.info("Festività aggiornata con successo: {}", updatedHoliday.getId());

        return convertToDTO(updatedHoliday);
    }

    /**
     * Elimina (disattiva) una festività
     */
    @Transactional
    public void deleteHoliday(Long id) {
        log.info("Eliminazione festività: {}", id);

        CompanyHoliday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Festività non trovata con id: " + id));

        holiday.setActive(false);
        holidayRepository.save(holiday);

        log.info("Festività disattivata con successo: {}", id);
    }

    /**
     * Converte entity in DTO
     */
    private CompanyHolidayDTO convertToDTO(CompanyHoliday holiday) {
        return CompanyHolidayDTO.builder()
                .id(holiday.getId())
                .date(holiday.getDate())
                .name(holiday.getName())
                .description(holiday.getDescription())
                .type(holiday.getType().name())
                .typeDisplayName(holiday.getType().getDisplayName())
                .recurring(holiday.getRecurring())
                .active(holiday.getActive())
                .createdAt(holiday.getCreatedAt())
                .updatedAt(holiday.getUpdatedAt())
                .build();
    }
}




