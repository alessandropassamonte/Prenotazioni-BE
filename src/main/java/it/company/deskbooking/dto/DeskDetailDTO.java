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
public class DeskDetailDTO {
    private Long id;
    private String deskNumber;
    private FloorDTO floor;
    private DepartmentDTO department;
    private Desk.DeskType type;
    private Desk.DeskStatus status;
    private Double positionX;
    private Double positionY;
    private String equipment;
    private String notes;
    private Boolean nearWindow;
    private Boolean nearElevator;
    private Boolean nearBreakArea;
    private Boolean active;
    private BookingDTO currentBooking;
}
