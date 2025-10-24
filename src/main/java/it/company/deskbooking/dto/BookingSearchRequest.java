package it.company.deskbooking.dto;

import it.company.deskbooking.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSearchRequest {
    private Long userId;
    private Long deskId;
    private Long floorId;
    private Long departmentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Booking.BookingStatus status;
}
