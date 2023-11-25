package com.example.batch.repository;

import com.example.batch.job.model.base.ApiBatchJobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiBatchJobInfoRepository extends JpaRepository<ApiBatchJobInfo,String> {
    ApiBatchJobInfo findByExecuteName(String executeName);

}
