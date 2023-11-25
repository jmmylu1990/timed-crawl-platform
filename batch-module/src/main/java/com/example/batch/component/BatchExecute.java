package com.example.batch.component;

import com.example.batch.job.model.CityAndInterCityBus;
import com.example.batch.job.model.base.ApiBatchJobInfo;
import com.example.batch.repository.ApiBatchJobInfoRepository;
import com.example.batch.utils.ClassUtils;
import com.example.batch.utils.DateUtils;
import org.jsoup.select.Evaluator;
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

    public String run(String executeName) throws Exception {

        String batchDownloadPath = System.getProperty("user.home") + downloadPath;
        ApiBatchJobInfo apiBatchJobInfo = apiBatchJobInfoRepository.findByExecuteName(executeName);
        String jobType = apiBatchJobInfo.getJobType().getType();
        String dataFormat = apiBatchJobInfo.getDataFormat().getFormat();
        String className = apiBatchJobInfo.getClassName();
        Long chunkSize = apiBatchJobInfo.getChunkSize().longValue();
        String apiResourceURL = apiBatchJobInfo.getResourceURL();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("executeName", executeName)
                .addString("jobType", jobType)
                .addString("dataFormat", dataFormat)
                .addString("className", className)
                .addLong("chunkSize", chunkSize)
                .addString("resourceURL", apiResourceURL)
                .addLong("time", System.currentTimeMillis())
                .addString("filePath", batchDownloadPath)
                .addString("jobFileName", String.format("%s%s.txt", executeName, DateUtils.now("yyyyMMddHHmmss")))
                .toJobParameters();

        String jobName = String.format("%sJob", jobType);
        Job job = jobs.get(jobName);

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);

        return jobExecution.getExitStatus().getExitCode();
    }
}
