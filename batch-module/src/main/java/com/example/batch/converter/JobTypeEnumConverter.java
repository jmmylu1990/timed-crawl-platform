package com.example.batch.converter;

import com.example.batch.enums.DataFormatEnum;
import com.example.batch.enums.JobTypeEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


@Converter(autoApply = true)
public class JobTypeEnumConverter implements AttributeConverter<JobTypeEnum, String> {

    @Override
    public String convertToDatabaseColumn(JobTypeEnum attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public JobTypeEnum convertToEntityAttribute(String dbData) {
        return dbData == null ? null : JobTypeEnum.valueOf(dbData);
    }
}
