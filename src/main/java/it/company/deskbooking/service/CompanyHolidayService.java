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
import java.util.List;
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
