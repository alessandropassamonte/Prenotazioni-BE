package it.company.deskbooking.controller;

import it.company.deskbooking.dto.*;
import it.company.deskbooking.service.DeskService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/desks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeskController {

    private final DeskService deskService;

    @GetMapping
    public ResponseEntity<List<DeskDTO>> getAllDesks() {
        List<DeskDTO> desks = deskService.getAllDesks();
        return ResponseEntity.ok(desks);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DeskDTO>> getAllActiveDesks() {
        List<DeskDTO> desks = deskService.getAllActiveDesks();
        return ResponseEntity.ok(desks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeskDetailDTO> getDeskById(@PathVariable Long id) {
        DeskDetailDTO desk = deskService.getDeskById(id);
        return ResponseEntity.ok(desk);
    }

    @GetMapping("/floor/{floorId}")
    public ResponseEntity<List<DeskDTO>> getDesksByFloor(@PathVariable Long floorId) {
        List<DeskDTO> desks = deskService.getDesksByFloor(floorId);
        return ResponseEntity.ok(desks);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<DeskDTO>> getDesksByDepartment(@PathVariable Long departmentId) {
        List<DeskDTO> desks = deskService.getDesksByDepartment(departmentId);
        return ResponseEntity.ok(desks);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DeskDTO>> getAvailableDesks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long floorId,
            @RequestParam(required = false) Long departmentId) {
        
        List<DeskDTO> desks;
        
        if (floorId != null) {
            desks = deskService.getAvailableDesksForDateAndFloor(date, floorId);
        } else if (departmentId != null) {
            desks = deskService.getAvailableDesksForDateAndDepartment(date, departmentId);
        } else {
            desks = deskService.getAvailableDesksForDate(date);
        }
        
        return ResponseEntity.ok(desks);
    }

    @PostMapping
    public ResponseEntity<DeskDTO> createDesk(@RequestBody CreateDeskRequest request) {
        DeskDTO desk = deskService.createDesk(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(desk);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeskDTO> updateDesk(
            @PathVariable Long id,
            @RequestBody UpdateDeskRequest request) {
        DeskDTO desk = deskService.updateDesk(id, request);
        return ResponseEntity.ok(desk);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesk(@PathVariable Long id) {
        deskService.deleteDesk(id);
        return ResponseEntity.noContent().build();
    }
}
