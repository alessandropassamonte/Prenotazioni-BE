package it.company.deskbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorStatisticsDTO {
    private Long totalDesks;
    private Long availableDesks;
    private Long occupiedDesks;
    private Long totalLockers;
    private Long freeLockers;
    private Long assignedLockers;
    private Double occupancyRate;
}
