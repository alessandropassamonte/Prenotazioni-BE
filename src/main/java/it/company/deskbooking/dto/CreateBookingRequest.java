package it.company.deskbooking.dto;

import it.company.deskbooking.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    private Long deskId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Booking.BookingType type;
    private String notes;
}
