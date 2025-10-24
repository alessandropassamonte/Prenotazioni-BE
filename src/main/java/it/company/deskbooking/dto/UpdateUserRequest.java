package it.company.deskbooking.dto;

import it.company.deskbooking.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private Long departmentId;
    private User.UserRole role;
    private User.WorkType workType;
    private String phoneNumber;
    private String avatarUrl;
    private Boolean active;
}
