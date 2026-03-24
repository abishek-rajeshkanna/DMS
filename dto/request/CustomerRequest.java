package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotNull(message = "Dealership ID is required")
    private Long dealershipId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[+\\d][\\d\\s\\-().]{6,19}$", message = "Invalid phone number")
    private String phone;

    private CustomerStatus status;

    private String source;

    private Long assignedToUserId;

    private String notes;
}
