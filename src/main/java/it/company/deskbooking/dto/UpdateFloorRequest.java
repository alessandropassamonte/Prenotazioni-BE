package it.company.deskbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFloorRequest {
    private String name;
    private Integer squareMeters;
    private String description;
    private String mapImageUrl;
    private Boolean active;
}
