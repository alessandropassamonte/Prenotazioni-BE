package it.company.deskbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String employeeId;
    private Long departmentId;
    private String departmentName;
    private String role;
    private String workType;
    private String phoneNumber;
    private String avatarUrl;
    private Boolean active;
    private Boolean emailVerified;
    private LocalDateTime lastLoginAt;
    private Integer totalBookings;
    private Integer activeBookings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
