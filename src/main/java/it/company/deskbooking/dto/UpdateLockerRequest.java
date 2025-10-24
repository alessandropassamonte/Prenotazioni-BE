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
public class UpdateLockerRequest {
    private Locker.LockerType type;
    private Locker.LockerStatus status;
    private String notes;
    private Boolean active;
}
