package it.company.deskbooking.dto;

import it.company.deskbooking.model.Desk.DeskStatus;
import it.company.deskbooking.model.Desk.DeskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeskDTO {
    private Long id;
    private String deskNumber;
    private Long floorId;
    private String floorName;
    private Long departmentId;
    private String departmentName;
    private DeskType type;
    private DeskStatus status;
    private Double positionX;
    private Double positionY;
    private String equipment;
    private String notes;
    private Boolean nearWindow;
    private Boolean nearElevator;
    private Boolean nearBreakArea;
    private Boolean active;
    private Boolean availableForDate; // Per una specifica data
}

