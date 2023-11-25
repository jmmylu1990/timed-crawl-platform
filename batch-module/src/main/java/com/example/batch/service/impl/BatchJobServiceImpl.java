package com.example.batch.service.impl;

import com.example.batch.component.BatchExecute;
import com.example.batch.service.BatchJobService;
import com.example.batch.utils.DateUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BatchJobServiceImpl implements BatchJobService {


    @Autowired
    private BatchExecute batchExecute;

    @Override
    public String executeJob(String executeName) throws Exception {
        return batchExecute.run(executeName);
    }
}
