package com.example.quartz.job;

import com.example.quartz.JobDataMapConstant;
import com.example.quartz.componet.AbstractJobExecutor;
import com.example.quartz.componet.ApiRewriteComponent;
import com.example.quartz.repository.ScheduleJobRepository;
import com.example.quartz.enums.JobResultEnum;
import com.example.quartz.model.entity.ScheduleJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Slf4j
@Component
public class ApiTriggerJobExecutor extends AbstractJobExecutor {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ScheduleJobRepository scheduleJobRepository;

	@Autowired
	private ApiRewriteComponent apiRewriteComponent;
	
	@Override
	public void executiveCore(JobExecutionContext context) throws JobExecutionException {
		log.info("This is a API-Trigger executor");
		Date executeTime = new Date();
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		ScheduleJob ScheduleJob = (ScheduleJob) jobDataMap.get(JobDataMapConstant.SCHEDULE_JOB);
		ScheduleJob scheduleJob = scheduleJobRepository.findByJobId(ScheduleJob.getJobId());
		int errorAccumulation = scheduleJob.getErrorAccumulation();
		String apiUrl = apiRewriteComponent.normalize(scheduleJob.getJobStrategy());
		
		try {
			scheduleJob.setLastFireTime(executeTime);
			scheduleJob.setLastResult(JobResultEnum.EXECUTING);
			scheduleJob.setNextFireTime(new CronExpression(scheduleJob.getCronExpression()).getNextValidTimeAfter(executeTime));
			scheduleJobRepository.save(scheduleJob); // Update scheduleJob state first
			
			// Fetch api and log response
			String response = restTemplate.getForObject(apiUrl, String.class);
			log.info("`{}` response: {}", apiUrl, response);
			scheduleJob.setErrorAccumulation(0);
			scheduleJob.setLastResult(JobResultEnum.SUCCESS);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// Set error accumulation
			scheduleJob.setLastResult(JobResultEnum.FAIL);
			scheduleJob.setErrorAccumulation(errorAccumulation + 1);

		} finally {
			Date completeTime = new Date();
			scheduleJob.setLastCompleteTime(completeTime);
			scheduleJobRepository.save(scheduleJob);
		}
	}

}
