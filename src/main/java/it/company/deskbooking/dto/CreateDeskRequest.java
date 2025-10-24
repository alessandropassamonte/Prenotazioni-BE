package it.company.deskbooking.dto;

import it.company.deskbooking.model.Desk;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeskRequest {
    private String deskNumber;
    private Long floorId;
    private Long departmentId;
    private Desk.DeskType type;
    private Double positionX;
    private Double positionY;
    private String equipment;
    private String notes;
    private Boolean nearWindow;
    private Boolean nearElevator;
    private Boolean nearBreakArea;
}
