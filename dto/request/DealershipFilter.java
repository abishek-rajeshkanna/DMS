package com.hyundai.DMS.dto.request;

import lombok.Data;

@Data
public class DealershipFilter {
    private String name;
    private String city;
    private String state;
    private Boolean isActive;
}
