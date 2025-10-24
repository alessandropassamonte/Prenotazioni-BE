package it.company.deskbooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_date", columnList = "booking_date"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_desk_id", columnList = "desk_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desk_id", nullable = false)
    private Desk desk;

    @Column(nullable = false)
    private LocalDate bookingDate;

    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.ACTIVE;

    @Column(length = 1000)
    private String notes;

    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;

    @Enumerated(EnumType.STRING)
    private BookingType type;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime cancelledAt;
    private String cancellationReason;

    public enum BookingStatus {
        ACTIVE,         // Prenotazione attiva
        CHECKED_IN,     // Utente ha fatto check-in
        CHECKED_OUT,    // Utente ha fatto check-out
        COMPLETED,      // Prenotazione completata
        CANCELLED,      // Prenotazione cancellata
        NO_SHOW        // Utente non si è presentato
    }

    public enum BookingType {
        FULL_DAY,
        MORNING,
        AFTERNOON,
        CUSTOM
    }

    public boolean isActive() {
        return status == BookingStatus.ACTIVE || status == BookingStatus.CHECKED_IN;
    }

    public boolean canCheckIn() {
        return status == BookingStatus.ACTIVE && 
               bookingDate.equals(LocalDate.now());
    }

    public boolean canCheckOut() {
        return status == BookingStatus.CHECKED_IN;
    }
}
