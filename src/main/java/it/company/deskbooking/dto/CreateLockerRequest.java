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
public class CreateLockerRequest {
    private String lockerNumber;
    private Long floorId;
    private Locker.LockerType type;
    private Double positionX;
    private Double positionY;
    private String notes;
}
