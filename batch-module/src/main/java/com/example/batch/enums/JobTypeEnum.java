package com.example.batch.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum JobTypeEnum {
    GENERAL("general"),
    TDX("tdx"),
    PTX("ptx");

    private final String type;

    public static JobTypeEnum fromType(String type) {
        return Stream.of(JobTypeEnum.values())
                .filter(jobType -> jobType.getType().equals(type))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
