package it.company.deskbooking.dto;

import it.company.deskbooking.model.CompanyHoliday;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO per CompanyHoliday Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyHolidayDTO {
    private Long id;
    private LocalDate date;
    private String name;
    private String description;
    private String type;
    private String typeDisplayName;
    private Boolean recurring;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

