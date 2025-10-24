package it.company.deskbooking.service;

import it.company.deskbooking.dto.*;
import it.company.deskbooking.exception.ResourceNotFoundException;
import it.company.deskbooking.model.Floor;
import it.company.deskbooking.repository.*;
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
public class FloorService {

    private final FloorRepository floorRepository;
    private final DeskRepository deskRepository;
    private final LockerRepository lockerRepository;
    private final BookingRepository bookingRepository;

    public List<FloorDTO> getAllFloors() {
        return floorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FloorDTO> getAllActiveFloors() {
        return floorRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FloorDetailDTO getFloorById(Long id) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piano non trovato con id: " + id));
        return convertToDetailDTO(floor);
    }

    public FloorDetailDTO getFloorByNumber(Integer floorNumber) {
        Floor floor = floorRepository.findByFloorNumber(floorNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Piano non trovato con numero: " + floorNumber));
        return convertToDetailDTO(floor);
    }

    @Transactional
    public FloorDTO createFloor(CreateFloorRequest request) {
        log.info("Creazione nuovo piano: {}", request.getName());

        Floor floor = Floor.builder()
                .name(request.getName())
                .floorNumber(request.getFloorNumber())
                .code(request.getCode())
                .squareMeters(request.getSquareMeters())
                .description(request.getDescription())
                .mapImageUrl(request.getMapImageUrl())
                .totalDesks(0)
                .totalLockers(0)
                .active(true)
                .build();

        Floor savedFloor = floorRepository.save(floor);
        log.info("Piano creato con successo: {}", savedFloor.getId());

        return convertToDTO(savedFloor);
    }

    @Transactional
    public FloorDTO updateFloor(Long id, UpdateFloorRequest request) {
        log.info("Aggiornamento piano: {}", id);

        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piano non trovato con id: " + id));

        if (request.getName() != null) {
            floor.setName(request.getName());
        }
        if (request.getSquareMeters() != null) {
            floor.setSquareMeters(request.getSquareMeters());
        }
        if (request.getDescription() != null) {
            floor.setDescription(request.getDescription());
        }
        if (request.getMapImageUrl() != null) {
            floor.setMapImageUrl(request.getMapImageUrl());
        }
        if (request.getActive() != null) {
            floor.setActive(request.getActive());
        }

        Floor updatedFloor = floorRepository.save(floor);
        log.info("Piano aggiornato con successo: {}", updatedFloor.getId());

        return convertToDTO(updatedFloor);
    }

    @Transactional
    public void deleteFloor(Long id) {
        log.info("Eliminazione piano: {}", id);

        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piano non trovato con id: " + id));

        floor.setActive(false);
        floorRepository.save(floor);

        log.info("Piano disattivato con successo: {}", id);
    }

    public FloorStatisticsDTO getFloorStatistics(Long floorId, LocalDate date) {
        Long totalDesks = deskRepository.countByFloorId(floorId);
        Long occupiedDesks = (long) bookingRepository.findActiveBookingsForDateAndFloor(date, floorId).size();
        Long availableDesks = totalDesks - occupiedDesks;
        
        Long totalLockers = (long) lockerRepository.findByFloorId(floorId).size();
        Long freeLockers = lockerRepository.countFreeLockersByFloor(floorId);
        Long assignedLockers = totalLockers - freeLockers;
        
        Double occupancyRate = totalDesks > 0 ? (double) occupiedDesks / totalDesks * 100 : 0.0;

        return FloorStatisticsDTO.builder()
                .totalDesks(totalDesks)
                .availableDesks(availableDesks)
                .occupiedDesks((long) occupiedDesks)
                .totalLockers(totalLockers)
                .freeLockers(freeLockers)
                .assignedLockers(assignedLockers)
                .occupancyRate(occupancyRate)
                .build();
    }

    private FloorDTO convertToDTO(Floor floor) {
        return FloorDTO.builder()
                .id(floor.getId())
                .name(floor.getName())
                .floorNumber(floor.getFloorNumber())
                .code(floor.getCode())
                .squareMeters(floor.getSquareMeters())
                .totalDesks(floor.getTotalDesks())
                .totalLockers(floor.getTotalLockers())
                .description(floor.getDescription())
                .mapImageUrl(floor.getMapImageUrl())
                .active(floor.getActive())
                .build();
    }

    private FloorDetailDTO convertToDetailDTO(Floor floor) {
        return FloorDetailDTO.builder()
                .id(floor.getId())
                .name(floor.getName())
                .floorNumber(floor.getFloorNumber())
                .code(floor.getCode())
                .squareMeters(floor.getSquareMeters())
                .totalDesks(floor.getTotalDesks())
                .totalLockers(floor.getTotalLockers())
                .description(floor.getDescription())
                .mapImageUrl(floor.getMapImageUrl())
                .active(floor.getActive())
                .statistics(getFloorStatistics(floor.getId(), LocalDate.now()))
                .build();
    }
}
