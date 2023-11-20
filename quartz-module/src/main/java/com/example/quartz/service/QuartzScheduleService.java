package com.example.quartz.service;

import com.example.quartz.JobDataMapConstant;
import com.example.quartz.repository.ScheduleJobRepository;
import com.example.quartz.enums.JobGroupEnum;
import com.example.quartz.enums.JobResultEnum;
import com.example.quartz.enums.StateEnum;
import com.example.quartz.job.ApiTriggerJobExecutor;
import com.example.quartz.model.entity.ScheduleJob;
import com.example.quartz.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@Transactional(transactionManager = "quartzTransactionManager")
public class QuartzScheduleService<T extends ScheduleJob> {

    private static final String FAILING_JOB_GROUP_NAME = "FailingJobsGroup";
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private ScheduleJobRepository scheduleJobRepository;
    private Scheduler scheduler;
    @PostConstruct
    public void init() {
        scheduler = schedulerFactoryBean.getScheduler();
    }


    public List<ScheduleJob> dispatcher(ScheduleJob... jobs) {
        return Arrays.stream(jobs).parallel().map(job -> {
            try {
                String jobId = job.getJobId();
                String jobName = job.getJobName();
                StateEnum jobStatus = job.getJobStatus();
                boolean jobExisted = this.isJobPresent(job);
                if (jobExisted && jobStatus == StateEnum.DISABLED) {
                    this.removeJob(job);
                    job.setNextFireTime(null);
                    job.setLastResult(JobResultEnum.AWAITING);
                    log.info("Job [id: {} / name: {}] destroy completely!", jobId, jobName);
                } else if (!jobExisted && jobStatus == StateEnum.ENABLED) {
                    Date nextFireTime = this.refreshJob(job, true);
                    job.setNextFireTime(nextFireTime);
                    log.info("Job [id: {} / name: {} / nextFireTime: {}] init completely!", jobId, jobName, DateUtils.formatDateToStr(nextFireTime));
                }
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
            } finally {
                scheduleJobRepository.save((T) job);
            }

            return job;
        }).collect(Collectors.toList());
    }

    public List<ScheduleJob> dispatcher(String jobGroup, String... jobIds) {
        List<T> jobList = scheduleJobRepository.findByJobGroup(jobGroup);
        List<String> jobIdList = Arrays.asList(jobIds);
        ScheduleJob[] matchJobs = jobList.parallelStream()
                .filter(job -> jobIdList.isEmpty() || jobIdList.contains(job.getJobId()))
                .toArray(ScheduleJob[]::new);

        return this.dispatcher(matchJobs);
    }

    public boolean isJobPresent(ScheduleJob scheduleJob) {
        try {
            return scheduler.checkExists(
                    JobKey.jobKey(
                            scheduleJob.getJobId(),
                            scheduleJob.getJobGroup()
                    )
            );
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }

    public boolean isTriggerPresent(ScheduleJob scheduleJob) {
        try {
            return scheduler.checkExists(
                    TriggerKey.triggerKey(
                            scheduleJob.getJobId(),
                            scheduleJob.getJobGroup()
                    )
            );
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }

    public void triggerNow(String jobId, boolean createIfNotExisted) throws SchedulerException {
        T scheduleJob = (T)scheduleJobRepository.findByJobId(jobId);
        if (Objects.nonNull(scheduleJob)) {
            this.removeFailingRetryJobs(scheduleJob);
            JobKey jobKey = JobKey.jobKey(scheduleJob.getJobId(), scheduleJob.getJobGroup());
            if (createIfNotExisted && !scheduler.checkExists(jobKey)) {
                this.updateStatus(jobId, StateEnum.ENABLED.getCode());
            }
            scheduler.triggerJob(jobKey);
        }
    }

    public void triggerNow(String jobId) throws SchedulerException {
        this.triggerNow(jobId, true);
    }

    public boolean stopNow(String... jobIds) throws SchedulerException {
        List<JobExecutionContext> currentlyExecuting = scheduler.getCurrentlyExecutingJobs();
        List<String> jobIdList = Arrays.asList(jobIds);
        long interruptCount = 0;
        for (JobExecutionContext context : currentlyExecuting) {
            ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get(JobDataMapConstant.SCHEDULE_JOB);
            if (Objects.isNull(scheduleJob)) continue;

            String jobId = scheduleJob.getJobId();
            if (jobIdList.contains(jobId)) {
                boolean isSuccess = scheduler.interrupt(context.getJobDetail().getKey());
                if (isSuccess) {
                    interruptCount++;
                    scheduleJob.setLastResult(JobResultEnum.AWAITING);
                    scheduleJobRepository.save((T) scheduleJob);
                } else {
                    jobIdList.remove(jobId);
                    interruptCount -= 100;
                }
            }
        }

        return interruptCount > 0;
    }

    public Date updateCron(String jobId, String cron) throws SchedulerException {
        Date nextFireTime = null;
        try {
            T scheduleJob = (T) scheduleJobRepository.findByJobId(jobId);
            if (Objects.isNull(scheduleJob)) return nextFireTime;

            String jobName = scheduleJob.getJobName();
            String jobGroup = scheduleJob.getJobGroup();
            StateEnum jobStatus = scheduleJob.getJobStatus();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobId, jobGroup);
            CronTriggerImpl trigger = new CronTriggerImpl();
            trigger.setCronExpression(cron);
            trigger.setJobName(jobName);
            trigger.setKey(triggerKey);
            // If the trigger existed then update it, otherwise create it
            if (scheduler.checkExists(triggerKey)) {
                nextFireTime = scheduler.rescheduleJob(triggerKey, trigger);
            } else {
                nextFireTime = scheduler.scheduleJob(trigger);
            }
            if (jobStatus == StateEnum.DISABLED) scheduler.unscheduleJob(triggerKey);
            if (Objects.nonNull(nextFireTime)) {
                scheduleJob.setCronExpression(cron);
                scheduleJob.setNextFireTime(nextFireTime);
                scheduleJobRepository.save(scheduleJob);
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }

        return nextFireTime;
    }

    public Integer removeFailingRetryJobs(ScheduleJob scheduleJob) {
        // Check the `FailingJobsGroup` exists or not, if existed then delete it.
        return (int) IntStream.range(0, scheduleJob.getRefireMaxCount())
                .mapToObj(index -> JobKey.jobKey(String.format("%s[%d]", scheduleJob.getJobName(), index), FAILING_JOB_GROUP_NAME))
                .filter(failingJobKey -> {
                    try {
                        if (scheduler.checkExists(failingJobKey) && scheduler.deleteJob(failingJobKey)) {
                            scheduleJob.setLastResult(JobResultEnum.AWAITING);
                            scheduleJobRepository.save((T) scheduleJob);
                            log.info("Remove the failing job: {}", failingJobKey);
                            return true;
                        }
                        return false;
                    } catch (SchedulerException e) {
                        log.error(e.getMessage(), e);
                    }
                    return false;
                }).count();
    }

    public int removeFailingRetryJobs(String jobId) {
        return this.removeFailingRetryJobs(scheduleJobRepository.findByJobId(jobId));
    }

    public Date refreshJob(ScheduleJob scheduleJob, boolean createIfNoTrigger) throws SchedulerException {
        String jobId = scheduleJob.getJobId();
        String jobGroup = scheduleJob.getJobGroup();
        JobDetail jobDetail = this.determineJobDetail(jobId, jobGroup);
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put(JobDataMapConstant.SCHEDULE_JOB, scheduleJob);

        TriggerKey triggerKey = TriggerKey.triggerKey(jobId, jobGroup);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        // Cron expression builder with misfire application.properties
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        CronScheduleBuilder missFiredHandleStrategy = scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
        if (Objects.isNull(trigger) && createIfNoTrigger) {
            // Set the new cron expression
            trigger = triggerBuilder
                    .withIdentity(jobId, jobGroup)
                    .withSchedule(missFiredHandleStrategy)
                    .build();

            return scheduler.scheduleJob(jobDetail, trigger);
        } else if (Objects.nonNull(trigger)) {
            // Update the new cron expression
            trigger = trigger.getTriggerBuilder()
                    .withIdentity(triggerKey)
                    .withSchedule(missFiredHandleStrategy)
                    .build();
            // Reset the schedule
            return scheduler.rescheduleJob(triggerKey, trigger);
        } else {
            throw new SchedulerException("The job trigger not existed!");
        }
    }

    public Date refreshJob(T scheduleJob) throws SchedulerException {
        return refreshJob(scheduleJob, true);
    }

    public Date refreshJob(String jobId, boolean createIfNoTrigger) throws SchedulerException {
        return refreshJob(scheduleJobRepository.findByJobId(jobId), createIfNoTrigger);
    }

    public Date refreshJob(String jobId) throws SchedulerException {
        return refreshJob(jobId, true);
    }

    public boolean removeJob(ScheduleJob scheduleJob) throws SchedulerException {
        // Check the `FailingJobsGroup` exists or not, if existed then delete it.
        this.removeFailingRetryJobs(scheduleJob);
        String jobId = scheduleJob.getJobId();
        String jobGroup = scheduleJob.getJobGroup();
        if (this.isTriggerPresent(scheduleJob)) {
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobId, jobGroup));
        }
        return scheduler.deleteJob(JobKey.jobKey(jobId, jobGroup));
    }

    public boolean removeJob(String jobId) throws SchedulerException {
        return removeJob(scheduleJobRepository.findByJobId(jobId));
    }

    public Date updateStatus(String jobId, Integer status) throws SchedulerException {
        T scheduleJob = (T) scheduleJobRepository.findByJobId(jobId);
        if (Objects.isNull(scheduleJob)) return null;

        Date nextFireTime = scheduleJob.getNextFireTime();
        String jobName = scheduleJob.getJobName();
        StateEnum stateEnum = Objects.nonNull(status) ? StateEnum.fromCode(status) : StateEnum.toggle(scheduleJob.getJobStatus());
        if (stateEnum == StateEnum.DISABLED) {
            log.info("Remove the job `{}`", jobName);
            this.removeJob(scheduleJob);
            // Set 0(wait trigger) to last result
            scheduleJob.setLastResult(JobResultEnum.AWAITING);
        } else {
            nextFireTime = this.refreshJob(scheduleJob);
            log.info("Update the job `{}` with next fire time: {}", jobName, nextFireTime);
        }
        scheduleJob.setJobStatus(stateEnum);
        scheduleJob.setNextFireTime(nextFireTime);
        scheduleJobRepository.save(scheduleJob);

        return nextFireTime;
    }

    private JobDetail determineJobDetail(String jobId, String jobGroup) {
        switch (JobGroupEnum.fromName(jobGroup)) {
            case API_TRIGGER_GROUP:
                return JobBuilder.newJob(ApiTriggerJobExecutor.class)
                        .withIdentity(jobId, jobGroup)
                        .build();
            default:
                throw new IllegalArgumentException("Not a valid job group!");
        }
    }
}
