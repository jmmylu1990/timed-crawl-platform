package com.example.quartz.repository;

import com.example.quartz.model.entity.ScheduleJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleJobRepository<T extends ScheduleJob> extends JpaRepository<ScheduleJob,String> {
    public T findByJobId(String jobId);
    public List<T> findByJobGroup(String jobGroup);
}
