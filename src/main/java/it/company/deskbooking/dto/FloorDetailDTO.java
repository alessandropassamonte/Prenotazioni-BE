package it.company.deskbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorDetailDTO {
    private Long id;
    private String name;
    private Integer floorNumber;
    private String code;
    private Integer squareMeters;
    private Integer totalDesks;
    private Integer totalLockers;
    private String description;
    private String mapImageUrl;
    private Boolean active;
    private List<DeskDTO> desks;
    private List<DepartmentDTO> departments;
    private FloorStatisticsDTO statistics;
}
