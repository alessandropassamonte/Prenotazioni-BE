package it.company.deskbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLockerAssignmentRequest {
    private Long userId;
    private Long lockerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
    private String accessCode;
}
