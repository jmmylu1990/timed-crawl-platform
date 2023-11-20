import com.example.quartz.QuartzModuleApplication;
import com.example.quartz.repository.ScheduleJobRepository;
import com.example.quartz.model.entity.ScheduleJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = QuartzModuleApplication.class)
public class QuartzModuleApplicationTest {

    @Autowired
    private ScheduleJobRepository scheduleJobRepository;

    @Test
    public void testStrategy() throws Exception {
        String jobId = "123";
        ScheduleJob scheduleJob = scheduleJobRepository.findByJobId(jobId);

        System.out.println(scheduleJob.getJobName());

    }
}
