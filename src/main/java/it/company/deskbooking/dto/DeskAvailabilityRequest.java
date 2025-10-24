package it.company.deskbooking.dto;

import it.company.deskbooking.model.Desk;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeskAvailabilityRequest {
    private LocalDate date;
    private Long floorId;
    private Long departmentId;
    private Desk.DeskType type;
}
