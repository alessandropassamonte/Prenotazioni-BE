package it.company.deskbooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lockers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"locker_number", "floor_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Locker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String lockerNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LockerType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LockerStatus status = LockerStatus.FREE;

    private Double positionX;
    private Double positionY;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "locker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LockerAssignment> assignments = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Boolean active = true;

    public enum LockerType {
        TURNISTI,  // Per turnisti
        FREE       // Liberi
    }

    public enum LockerStatus {
        FREE,
        ASSIGNED,
        MAINTENANCE
    }
}
