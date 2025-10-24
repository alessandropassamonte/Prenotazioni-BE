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
@Table(name = "desks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"desk_number", "floor_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Desk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deskNumber; // es: "1", "79", "P1-23"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeskType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DeskStatus status = DeskStatus.AVAILABLE;

    private Double positionX; // Coordinate X sulla mappa
    private Double positionY; // Coordinate Y sulla mappa

    @Column(length = 500)
    private String equipment; // es: "Monitor doppio, Docking station"

    @Column(length = 500)
    private String notes;

    private Boolean nearWindow = false;
    private Boolean nearElevator = false;
    private Boolean nearBreakArea = false;

    @OneToMany(mappedBy = "desk", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Boolean active = true;

    public enum DeskType {
        STANDARD,
        HOT_DESK,
        MEETING_ROOM,
        COLLABORATIVE_AREA
    }

    public enum DeskStatus {
        AVAILABLE,
        OCCUPIED,
        MAINTENANCE,
        RESERVED
    }
}
