package com.example.batch.job.model.base;

import com.example.batch.converter.DataFormatEnumConverter;
import com.example.batch.converter.JobTypeEnumConverter;
import com.example.batch.enums.DataFormatEnum;
import com.example.batch.enums.JobTypeEnum;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@Entity
@Table(name = "api_batch_job_info")
public @Data class ApiBatchJobInfo {
    @Id
    @Column(name = "execute_name")
    private String executeName;
    @Column(name = "job_type", nullable = false)
    @Convert(converter = JobTypeEnumConverter.class)
    private JobTypeEnum jobType;
    @Column(name = "data_format", nullable = false)
    @Convert(converter = DataFormatEnumConverter.class)
    private DataFormatEnum dataFormat;
    @Column(name= "class_name", nullable = false)
    private String className;
    @Column(name= "chunk_size", nullable = false)
    private Integer chunkSize;
    @Column(name = "resource_url", nullable = false)
    private String resourceURL;

}
