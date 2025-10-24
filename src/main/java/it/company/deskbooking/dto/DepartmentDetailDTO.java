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
public class DepartmentDetailDTO {
    private Long id;
    private String name;
    private String code;
    private Integer totalDesks;
    private String description;
    private FloorDTO floor;
    private List<DeskDTO> desks;
    private Boolean active;
}
