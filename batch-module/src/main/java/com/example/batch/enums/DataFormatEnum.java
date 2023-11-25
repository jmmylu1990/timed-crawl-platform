package com.example.batch.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum DataFormatEnum {
    CSV("CSV"),
    JSON("JSON");

    private final String format;

    public static DataFormatEnum fromFormat(String format) {
        return Stream.of(DataFormatEnum.values())
                .filter(dataFormat -> dataFormat.getFormat().equals(format))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
