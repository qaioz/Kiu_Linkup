package com.kiu.linkup.userservice.dto;

import com.kiu.linkup.userservice.enums.Major;
import com.kiu.linkup.userservice.enums.Minor;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "Firstname is mandatory")
    private String firstName;
    @NotEmpty(message = "Lastname is mandatory")
    private String lastName;
    @Email(message = "Email must be valid")
    @NotEmpty(message = "Email is mandatory")
    private String email;
    @NotEmpty(message = "Password is mandatory")
    @Size(min = 6, max = 32, message = "Password must be 6 to 32 characters long")
    private String password;
    @NotNull(message = "Major is mandatory")
    @Enumerated(EnumType.STRING)
    private Major major;
    @Enumerated
    private Minor minor;
    @NotNull(message = "Year is mandatory")
    @Range(min = 1, max = 4, message = "Year must be between 1 and 4")
    private Integer year;
}