package it.company.deskbooking.dto;

import it.company.deskbooking.model.CompanyHoliday;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate; /**
 * DTO per creazione CompanyHoliday
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyHolidayRequest {
    @NotNull(message = "La data è obbligatoria")
    private LocalDate date;

    @NotBlank(message = "Il nome è obbligatorio")
    private String name;

    private String description;

    @NotNull(message = "Il tipo è obbligatorio")
    private CompanyHoliday.HolidayType type;

    @NotNull(message = "Specificare se ricorrente")
    private Boolean recurring;
}
