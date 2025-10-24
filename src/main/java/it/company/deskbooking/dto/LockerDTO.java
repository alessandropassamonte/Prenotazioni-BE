package it.company.deskbooking.dto;

import it.company.deskbooking.model.Locker.LockerStatus;
import it.company.deskbooking.model.Locker.LockerType;
import it.company.deskbooking.model.LockerAssignment.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerDTO {
    private Long id;
    private String lockerNumber;
    private Long floorId;
    private String floorName;
    private LockerType type;
    private LockerStatus status;
    private Double positionX;
    private Double positionY;
    private String notes;
    private Boolean active;
}

