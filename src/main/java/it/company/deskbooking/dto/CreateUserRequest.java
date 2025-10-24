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
public class CreateUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String password;
    private Long departmentId;
    private User.UserRole role;
    private User.WorkType workType;
    private String phoneNumber;
}
