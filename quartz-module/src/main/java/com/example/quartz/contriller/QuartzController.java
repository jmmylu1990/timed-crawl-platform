package com.example.quartz.contriller;

import com.example.quartz.model.entity.ScheduleJob;
import com.example.quartz.service.QuartzScheduleService;
import com.example.quartz.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/quartz/api")
public class QuartzController {

    @Autowired
    private QuartzScheduleService<ScheduleJob> quartzScheduleService;
    @GetMapping(value = { "/quartz", "/init" })
    public void quartz(
            @RequestParam(required = false, defaultValue = "API-Trigger-Group") String jobGroup) {
        log.info("-- Initialing all quartz jobs --");
        quartzScheduleService.dispatcher(jobGroup);
        log.info("-- Initialing all quartz jobs finished --");
    }

    @GetMapping(value = { "/quartz/{jobId}", "/init/{jobId}" })
    public void quartz(@PathVariable(name = "jobId") String jobId,
                       @RequestParam(required = false, defaultValue = "API-Trigger-Group") String jobGroup) {
        log.info("-- Initialing quartz job --");
        quartzScheduleService.dispatcher(jobGroup, jobId);
        log.info("-- Initialing quartz job finished --");
    }

    @GetMapping(value = { "/execute/{jobId}", "/run/{jobId}" })
    public void executeJob(@PathVariable(value = "jobId", required = true) String jobId) throws SchedulerException {
        quartzScheduleService.triggerNow(jobId);
    }

    @GetMapping(value = { "/remove/{jobId}", "/delete/{jobId}" })
    public void removeJob(@PathVariable(value = "jobId", required = true) String jobId) throws SchedulerException {
        quartzScheduleService.removeJob(jobId);
    }

    @GetMapping(value = { "/removeFailing/{jobId}", "/deleteFailing/{jobId}" })
    public int removeFailingJob(@PathVariable(value = "jobId", required = true) String jobId) {
        return quartzScheduleService.removeFailingRetryJobs(jobId);
    }

    @GetMapping(value = { "/pause/{jobId}", "/stop/{jobId}" })
    public boolean stopJob(@PathVariable(value = "jobId", required = true) String jobId) throws SchedulerException {
        return quartzScheduleService.stopNow(jobId) || this.removeFailingJob(jobId) > 0;
    }

    @GetMapping(value = "/updateCron/{jobId}")
    public String updateCron(
            @PathVariable(value = "jobId", required = true) String jobId,
            @RequestParam(value = "cron", required = true) String cron) throws SchedulerException {
        return DateUtils.formatDateToStr(quartzScheduleService.updateCron(jobId, cron));
    }

    @GetMapping(value = "/updateStatus/{jobId}")
    public String updateStatus(
            @PathVariable(value = "jobId", required = true) String jobId,
            @RequestParam(value = "status", required = false) Integer status) throws SchedulerException {
        return DateUtils.formatDateToStr(quartzScheduleService.updateStatus(jobId, status));
    }

}
