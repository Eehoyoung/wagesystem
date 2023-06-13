package com.example.wagesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Position {

    DEPUTY("ROLE_DEPUTY"),
    MANAGER("ROLE_MANAGER"),
    STAFF("ROLE_STAFF");

    private final String value;

}
