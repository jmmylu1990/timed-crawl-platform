import com.example.batch.BatchModuleApplication;

import com.example.batch.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TimeZone;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= BatchModuleApplication.class)
public class BatchModuleApplicationTest {

    @Autowired
    private Job importCityAndInterCityBusJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Value("${batch.download.path}")
    private String downloadPath;

    @Test
    void importCityAndInterCityBusJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        String batchDownloadPath = System.getProperty("user.home") + downloadPath;
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("jobName","cityAndInterCityBusJob")
                .addString("resourceURL","https://tdx.transportdata.tw/api/basic/v2/Bus/Vehicle?%24top=30&%24format=JSON")
                .addString("filePath",batchDownloadPath)
                .addString("jobFileName",String.format("%s%s.txt","cityAndInterCityBusJob", DateUtils.now("yyyyMMddHHmmss")))
                .toJobParameters();

        JobExecution jobExecution =  jobLauncher.run(importCityAndInterCityBusJob,jobParameters);
        ExitStatus exitStatus = jobExecution.getExitStatus();
        String exitCode = exitStatus.getExitCode();
        System.out.println("exitCode:"+exitCode);

    }
}
