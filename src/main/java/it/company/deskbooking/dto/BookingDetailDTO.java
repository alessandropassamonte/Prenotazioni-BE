package it.company.deskbooking.dto;

import it.company.deskbooking.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailDTO {
    private Long id;
    private UserDTO user;
    private DeskDTO desk;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Booking.BookingStatus status;
    private Booking.BookingType type;
    private String notes;
    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;
    private LocalDateTime createdAt;
    private String cancellationReason;
}
