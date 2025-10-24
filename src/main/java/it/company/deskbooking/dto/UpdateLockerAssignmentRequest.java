package it.company.deskbooking.dto;

import it.company.deskbooking.model.LockerAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLockerAssignmentRequest {
    private LocalDate endDate;
    private String notes;
    private LockerAssignment.AssignmentStatus status;
}
