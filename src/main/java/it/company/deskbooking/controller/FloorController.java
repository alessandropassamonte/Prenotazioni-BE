package it.company.deskbooking.controller;

import it.company.deskbooking.dto.*;
import it.company.deskbooking.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/floors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FloorController {

    private final FloorService floorService;

    @GetMapping
    public ResponseEntity<List<FloorDTO>> getAllFloors() {
        List<FloorDTO> floors = floorService.getAllFloors();
        return ResponseEntity.ok(floors);
    }

    @GetMapping("/active")
    public ResponseEntity<List<FloorDTO>> getAllActiveFloors() {
        List<FloorDTO> floors = floorService.getAllActiveFloors();
        return ResponseEntity.ok(floors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FloorDetailDTO> getFloorById(@PathVariable Long id) {
        FloorDetailDTO floor = floorService.getFloorById(id);
        return ResponseEntity.ok(floor);
    }

    @GetMapping("/number/{floorNumber}")
    public ResponseEntity<FloorDetailDTO> getFloorByNumber(@PathVariable Integer floorNumber) {
        FloorDetailDTO floor = floorService.getFloorByNumber(floorNumber);
        return ResponseEntity.ok(floor);
    }

    @PostMapping
    public ResponseEntity<FloorDTO> createFloor(@RequestBody CreateFloorRequest request) {
        FloorDTO floor = floorService.createFloor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(floor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FloorDTO> updateFloor(
            @PathVariable Long id,
            @RequestBody UpdateFloorRequest request) {
        FloorDTO floor = floorService.updateFloor(id, request);
        return ResponseEntity.ok(floor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFloor(@PathVariable Long id) {
        floorService.deleteFloor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<FloorStatisticsDTO> getFloorStatistics(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LocalDate targetDate = date != null ? date : LocalDate.now();
        FloorStatisticsDTO statistics = floorService.getFloorStatistics(id, targetDate);
        return ResponseEntity.ok(statistics);
    }
}
