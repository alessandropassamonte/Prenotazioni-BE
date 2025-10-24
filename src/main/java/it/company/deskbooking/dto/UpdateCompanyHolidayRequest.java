package it.company.deskbooking.dto;

import it.company.deskbooking.model.CompanyHoliday;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate; /**
 * DTO per aggiornamento CompanyHoliday
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyHolidayRequest {
    private LocalDate date;
    private String name;
    private String description;
    private CompanyHoliday.HolidayType type;
    private Boolean recurring;
    private Boolean active;
}
