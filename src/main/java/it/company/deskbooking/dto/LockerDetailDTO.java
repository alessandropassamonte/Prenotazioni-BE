package it.company.deskbooking.dto;

import it.company.deskbooking.model.Locker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerDetailDTO {
    private Long id;
    private String lockerNumber;
    private FloorDTO floor;
    private Locker.LockerType type;
    private Locker.LockerStatus status;
    private Double positionX;
    private Double positionY;
    private String notes;
    private Boolean active;
    private LockerAssignmentDTO currentAssignment;
}
