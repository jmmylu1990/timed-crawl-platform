package com.example.quartz.model.entity;

import com.example.quartz.enums.JobResultEnum;
import com.example.quartz.enums.StateEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
@Entity
@Table(name = "timed_crawl_platform_schedule_job")
public @Data class ScheduleJob implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 排程編號 **/
    @Id
    @Column(name = "job_id", length = 50, nullable = false)
    private String jobId;

    @Column(name = "job_strategy")
    private String jobStrategy;

    /** 排程名稱 **/
    @Column(name = "job_name", nullable = false)
    private String jobName;

    /** 排程描述 **/
    @Column(name = "job_desc")
    private String jobDesc;

    /** 排程分組 **/
    @Column(name = "job_group", nullable = false)
    private String jobGroup;

    /** 排程狀態 0禁用 1啟用 2刪除 **/
    @Column(name = "job_status")
    @Enumerated(EnumType.ORDINAL)
    private StateEnum jobStatus;

    /** 排程最後執行時間 **/
    @Column(name = "last_fire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastFireTime;

    /** 排程最後完成時間 **/
    @Column(name = "last_complete_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastCompleteTime;

    /** 排程最後執行結果 0失敗 1成功 **/
    @Column(name = "last_result")
    @Enumerated(EnumType.ORDINAL)
    private JobResultEnum lastResult;

    /** 排程下次執行時間 **/
    @Column(name = "next_fire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextFireTime;

    /** 排程運行時間Cron表達式 **/
    @Column(name = "cron")
    private String cronExpression;

    /** 重新執行次數 **/
    @Column(name = "refire_max_count")
    private int refireMaxCount;

    /** 重新執行區間 **/
    @Column(name = "refire_interval")
    private int refireInterval;
    /** 錯誤累積次數 **/

    @Column(name = "error_accumulation")
    private int errorAccumulation;


}
