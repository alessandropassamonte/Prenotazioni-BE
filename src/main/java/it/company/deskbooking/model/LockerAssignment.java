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

@Entity
@Table(name = "locker_assignments", indexes = {
    @Index(name = "idx_locker_assignment_dates", columnList = "start_date, end_date"),
    @Index(name = "idx_locker_assignment_user", columnList = "user_id"),
    @Index(name = "idx_locker_assignment_locker", columnList = "locker_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LockerAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locker_id", nullable = false)
    private Locker locker;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate; // null per assegnazioni permanenti

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.ACTIVE;

    @Column(length = 1000)
    private String notes;

    private String accessCode; // Eventuale codice di accesso

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum AssignmentStatus {
        ACTIVE,
        EXPIRED,
        REVOKED
    }

    public boolean isCurrentlyActive() {
        if (status != AssignmentStatus.ACTIVE) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        
        if (now.isBefore(startDate)) {
            return false;
        }
        
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }
        
        return true;
    }
}
