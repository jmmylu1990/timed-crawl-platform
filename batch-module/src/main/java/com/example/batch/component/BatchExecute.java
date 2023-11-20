package com.example.batch.component;

import com.example.batch.job.model.base.ApiBatchJobInfo;
import com.example.batch.repository.ApiBatchJobInfoRepository;
import com.example.batch.utils.DateUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class BatchExecute {
    @Autowired
    private JobLauncher jobLauncher;

    @Value("${batch.download.path}")
    private String downloadPath;

    @Autowired
    private Map<String, Job> jobs;

    @Autowired
    private ApiBatchJobInfoRepository apiBatchJobInfoRepository;

    public String run(String jobName) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        String batchDownloadPath = System.getProperty("user.home") + downloadPath;
        ApiBatchJobInfo apiBatchJobInfo = apiBatchJobInfoRepository.findByJobName(jobName);
        String apiJobName = apiBatchJobInfo.getJobName();
        String apiResourceURL = apiBatchJobInfo.getResourceURL();

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("jobName",apiJobName)
                .addString("resourceURL",apiResourceURL)
                .addString("filePath",batchDownloadPath)
                .addString("jobFileName",String.format("%s%s.txt",apiJobName, DateUtils.now("yyyyMMddHHmmss")))
                .toJobParameters();
        Job job = jobs.get(apiJobName);
        JobExecution jobExecution = jobLauncher.run(job,jobParameters);

        return jobExecution.getExitStatus().getExitCode();
    }
}
