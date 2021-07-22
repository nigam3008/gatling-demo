package com.demo.galting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private String fName;
    private String lName;
    private String country;
}
