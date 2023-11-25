package com.example.batch.job.model.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@MappedSuperclass
public @Data class JsonJobModel extends AbstractJobModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
