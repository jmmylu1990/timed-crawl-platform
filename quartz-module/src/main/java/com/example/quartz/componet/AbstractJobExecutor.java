package com.example.quartz.componet;


import com.example.quartz.JobDataMapConstant;
import com.example.quartz.model.entity.ScheduleJob;
import com.example.quartz.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class AbstractJobExecutor implements InterruptableJob {

	/**
	 *  Current thread (For method `interrupt` use)
	 */
	private Thread currentThread;
	
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		if (Objects.nonNull(this.currentThread)) {
			this.currentThread.interrupt();
		}
	}
	
	/*
	 * @see org.quartz.InterruptableJob#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// Set the current thread
		this.currentThread = Thread.currentThread();
		Date executeTime = new Date(); // The trigger time as the execute time
		long startTime = executeTime.getTime();
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        ScheduleJob scheduleJob = (ScheduleJob) jobDataMap.get(JobDataMapConstant.SCHEDULE_JOB);
        int refireCount = (Integer) jobDataMap.getOrDefault(JobDataMapConstant.FIRE_INDEX, 0);
		boolean isFirstTime = refireCount == 0;
		String jobName = scheduleJob.getJobName();
		int refireMaxCount = scheduleJob.getRefireMaxCount();

        if (isFirstTime) {
			// If the schedule is the first triggered, then log it
			log.info("-- 排程【{}】執行開始 --", jobName);
		} else if (refireCount < refireMaxCount) {
			// If the schedule is not the first and last triggered, then log it
			log.warn("-- 排程【{}】第{}次重新執行 --", jobName, refireCount + 1);
		}
        try {
        	jobDataMap.put(JobDataMapConstant.EXECUTION_TIME, executeTime);
        	this.executiveCore(context);
        } finally {
        	log.info("-- 排程【{}】處理花費時間: {} --\n", jobName, DateUtils.formatUsageTime(System.currentTimeMillis() - startTime));
        }
	}
	
	/**
	 * 排程執行核心, 交由繼承的子類各自實作
	 * 
	 * @param context 排程執行所需上下文物件, JobExecutionContext 型別
	 * @throws JobExecutionException 
	 */
	protected abstract void executiveCore(JobExecutionContext context) throws JobExecutionException;


}
