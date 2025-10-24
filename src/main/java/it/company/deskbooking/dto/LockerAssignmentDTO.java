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
public class LockerAssignmentDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long lockerId;
    private String lockerNumber;
    private Long floorId;
    private String floorName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LockerAssignment.AssignmentStatus status;
    private String notes;
    private String accessCode;
}
