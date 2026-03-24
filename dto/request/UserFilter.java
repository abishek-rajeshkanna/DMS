package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.Role;
import com.hyundai.DMS.domain.enums.UserStatus;
import lombok.Data;

@Data
public class UserFilter {
    private Long dealershipId;
    private Role role;
    private UserStatus status;
    private String search;
}
