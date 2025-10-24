package it.company.deskbooking.service;

import it.company.deskbooking.dto.*;
import it.company.deskbooking.exception.ResourceNotFoundException;
import it.company.deskbooking.model.Desk;
import it.company.deskbooking.model.Department;
import it.company.deskbooking.model.Floor;
import it.company.deskbooking.repository.DeskRepository;
import it.company.deskbooking.repository.DepartmentRepository;
import it.company.deskbooking.repository.FloorRepository;
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
public class DeskService {

    private final DeskRepository deskRepository;
    private final FloorRepository floorRepository;
    private final DepartmentRepository departmentRepository;

    public List<DeskDTO> getAllDesks() {
        return deskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeskDTO> getAllActiveDesks() {
        return deskRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DeskDetailDTO getDeskById(Long id) {
        Desk desk = deskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postazione non trovata con id: " + id));
        return convertToDetailDTO(desk);
    }

    public List<DeskDTO> getDesksByFloor(Long floorId) {
        return deskRepository.findByFloorId(floorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeskDTO> getDesksByDepartment(Long departmentId) {
        return deskRepository.findByDepartmentId(departmentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeskDTO> getAvailableDesksForDate(LocalDate date) {
        return deskRepository.findAvailableDesksForDate(date).stream()
                .map(desk -> {
                    DeskDTO dto = convertToDTO(desk);
                    dto.setAvailableForDate(true);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DeskDTO> getAvailableDesksForDateAndFloor(LocalDate date, Long floorId) {
        return deskRepository.findAvailableDesksForDateAndFloor(date, floorId).stream()
                .map(desk -> {
                    DeskDTO dto = convertToDTO(desk);
                    dto.setAvailableForDate(true);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DeskDTO> getAvailableDesksForDateAndDepartment(LocalDate date, Long departmentId) {
        return deskRepository.findAvailableDesksForDateAndDepartment(date, departmentId).stream()
                .map(desk -> {
                    DeskDTO dto = convertToDTO(desk);
                    dto.setAvailableForDate(true);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public DeskDTO createDesk(CreateDeskRequest request) {
        log.info("Creazione nuova postazione: {}", request.getDeskNumber());

        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new ResourceNotFoundException("Piano non trovato"));

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dipartimento non trovato"));
        }

        Desk desk = Desk.builder()
                .deskNumber(request.getDeskNumber())
                .floor(floor)
                .department(department)
                .type(request.getType())
                .status(Desk.DeskStatus.AVAILABLE)
                .positionX(request.getPositionX())
                .positionY(request.getPositionY())
                .equipment(request.getEquipment())
                .notes(request.getNotes())
                .nearWindow(request.getNearWindow() != null ? request.getNearWindow() : false)
                .nearElevator(request.getNearElevator() != null ? request.getNearElevator() : false)
                .nearBreakArea(request.getNearBreakArea() != null ? request.getNearBreakArea() : false)
                .active(true)
                .build();

        Desk savedDesk = deskRepository.save(desk);
        
        // Aggiorna contatore totale desk del piano
        floor.setTotalDesks(deskRepository.countByFloorId(floor.getId()).intValue());
        floorRepository.save(floor);

        log.info("Postazione creata con successo: {}", savedDesk.getId());
        return convertToDTO(savedDesk);
    }

    @Transactional
    public DeskDTO updateDesk(Long id, UpdateDeskRequest request) {
        log.info("Aggiornamento postazione: {}", id);

        Desk desk = deskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postazione non trovata"));

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dipartimento non trovato"));
            desk.setDepartment(department);
        }

        if (request.getType() != null) {
            desk.setType(request.getType());
        }
        if (request.getStatus() != null) {
            desk.setStatus(request.getStatus());
        }
        if (request.getEquipment() != null) {
            desk.setEquipment(request.getEquipment());
        }
        if (request.getNotes() != null) {
            desk.setNotes(request.getNotes());
        }
        if (request.getNearWindow() != null) {
            desk.setNearWindow(request.getNearWindow());
        }
        if (request.getNearElevator() != null) {
            desk.setNearElevator(request.getNearElevator());
        }
        if (request.getNearBreakArea() != null) {
            desk.setNearBreakArea(request.getNearBreakArea());
        }
        if (request.getActive() != null) {
            desk.setActive(request.getActive());
        }

        Desk updatedDesk = deskRepository.save(desk);
        log.info("Postazione aggiornata con successo: {}", updatedDesk.getId());

        return convertToDTO(updatedDesk);
    }

    @Transactional
    public void deleteDesk(Long id) {
        log.info("Eliminazione postazione: {}", id);

        Desk desk = deskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postazione non trovata"));

        desk.setActive(false);
        deskRepository.save(desk);

        // Aggiorna contatore totale desk del piano
        Floor floor = desk.getFloor();
        floor.setTotalDesks(deskRepository.countByFloorId(floor.getId()).intValue());
        floorRepository.save(floor);

        log.info("Postazione disattivata con successo: {}", id);
    }

    private DeskDTO convertToDTO(Desk desk) {
        return DeskDTO.builder()
                .id(desk.getId())
                .deskNumber(desk.getDeskNumber())
                .floorId(desk.getFloor().getId())
                .floorName(desk.getFloor().getName())
                .departmentId(desk.getDepartment() != null ? desk.getDepartment().getId() : null)
                .departmentName(desk.getDepartment() != null ? desk.getDepartment().getName() : null)
                .type(desk.getType())
                .status(desk.getStatus())
                .positionX(desk.getPositionX())
                .positionY(desk.getPositionY())
                .equipment(desk.getEquipment())
                .notes(desk.getNotes())
                .nearWindow(desk.getNearWindow())
                .nearElevator(desk.getNearElevator())
                .nearBreakArea(desk.getNearBreakArea())
                .active(desk.getActive())
                .build();
    }

    private DeskDetailDTO convertToDetailDTO(Desk desk) {
        return DeskDetailDTO.builder()
                .id(desk.getId())
                .deskNumber(desk.getDeskNumber())
                .type(desk.getType())
                .status(desk.getStatus())
                .positionX(desk.getPositionX())
                .positionY(desk.getPositionY())
                .equipment(desk.getEquipment())
                .notes(desk.getNotes())
                .nearWindow(desk.getNearWindow())
                .nearElevator(desk.getNearElevator())
                .nearBreakArea(desk.getNearBreakArea())
                .active(desk.getActive())
                .build();
    }
}
