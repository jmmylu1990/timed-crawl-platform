package com.example.batch.job.model.base;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@Entity
@Table(name = "api_batch_job_info")
public @Data class ApiBatchJobInfo {

    @Id
    @Column(name = "job_name")
    private String jobName;
    @Column(name = "resource_type",nullable = false)
    private String resourceType;
    @Column(name = "resource_url",nullable = false)
    private String resourceURL;


}
