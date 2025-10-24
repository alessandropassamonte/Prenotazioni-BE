package it.company.deskbooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entità per la gestione dei giorni non lavorativi
 * (festività, chiusure aziendali, ecc.)
 */
@Entity
@Table(name = "company_holidays")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false, length = 200)
    private String name; // Nome della festività/chiusura

    @Column(columnDefinition = "TEXT")
    private String description; // Descrizione opzionale

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HolidayType type; // FESTIVITY, COMPANY_CLOSURE, MAINTENANCE, OTHER

    @Column(nullable = false)
    private Boolean recurring = false; // Se si ripete ogni anno (es. Natale)

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum HolidayType {
        FESTIVITY("Festività"),
        COMPANY_CLOSURE("Chiusura Aziendale"),
        MAINTENANCE("Manutenzione"),
        OTHER("Altro");

        private final String displayName;

        HolidayType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
