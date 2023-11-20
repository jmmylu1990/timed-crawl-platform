package com.example.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@EnableBatchProcessing
@Configuration
public class BatchJobConfig {
    @Autowired
    private JsonBatchJobConfig jsonBatchJobConfig;

}
