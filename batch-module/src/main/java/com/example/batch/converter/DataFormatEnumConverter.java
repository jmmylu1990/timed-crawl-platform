package com.example.batch.converter;

import com.example.batch.enums.DataFormatEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DataFormatEnumConverter implements AttributeConverter<DataFormatEnum, String> {

    @Override
    public String convertToDatabaseColumn(DataFormatEnum attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public DataFormatEnum convertToEntityAttribute(String dbData) {
        return dbData == null ? null : DataFormatEnum.valueOf(dbData);
    }

}
