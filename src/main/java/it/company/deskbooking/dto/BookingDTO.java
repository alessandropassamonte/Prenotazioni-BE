package it.company.deskbooking.dto;

import it.company.deskbooking.model.Booking.BookingStatus;
import it.company.deskbooking.model.Booking.BookingType;
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
public class BookingDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long deskId;
    private String deskNumber;
    private Long floorId;
    private String floorName;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingStatus status;
    private BookingType type;
    private String notes;
    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;
    private LocalDateTime createdAt;
}

