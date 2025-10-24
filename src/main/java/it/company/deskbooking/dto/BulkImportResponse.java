package it.company.deskbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; /**
 * DTO per response bulk import
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResponse {
    private Integer imported;
    private Integer skipped;
    private Integer errors;
    private java.util.List<String> errorMessages;
}
