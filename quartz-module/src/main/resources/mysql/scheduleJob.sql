-- customizedbatch.timed_crawl_platform_schedule_job definition

CREATE TABLE `timed_crawl_platform_schedule_job` (
  `job_id` varchar(50) NOT NULL,
  `job_name` varchar(255) NOT NULL,
  `job_desc` varchar(255) DEFAULT NULL,
  `cron` varchar(255) DEFAULT NULL,
  `job_strategy` varchar(255) DEFAULT NULL,
  `job_group` varchar(255) NOT NULL,
  `job_status` int DEFAULT NULL,
  `last_fire_time` datetime(6) DEFAULT NULL,
  `last_complete_time` datetime(6) DEFAULT NULL,
  `last_result` int DEFAULT NULL,
  `next_fire_time` datetime(6) DEFAULT NULL,
  `refire_max_count` int DEFAULT NULL,
  `refire_interval` int DEFAULT NULL,
  `error_accumulation` int DEFAULT NULL,
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO customizedbatch.api_batch_job_info
(job_name, resource_type, resource_url)
VALUES('cityAndInterCityBusJob', 'TDX', 'https://tdx.transportdata.tw/api/basic/v2/Bus/Vehicle?%24top=30&%24format=JSON');