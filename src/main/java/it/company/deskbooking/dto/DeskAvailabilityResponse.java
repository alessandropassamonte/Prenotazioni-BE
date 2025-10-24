package it.company.deskbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeskAvailabilityResponse {
    private Long deskId;
    private String deskNumber;
    private Boolean available;
    private BookingDTO existingBooking;
}
