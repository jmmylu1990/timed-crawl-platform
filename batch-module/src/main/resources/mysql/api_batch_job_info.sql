-- customizedbatch.api_batch_job_info definition
DROP TABLE IF EXISTS api_batch_job_info;
CREATE TABLE `api_batch_job_info` (
  `job_name` varchar(255) NOT NULL,
  `resource_type` varchar(255) NOT NULL,
  `resource_url` varchar(255) NOT NULL,
  PRIMARY KEY (`job_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO customizedbatch.api_batch_job_info
(job_name, resource_type, resource_url)
VALUES('cityAndInterCityBusJob', 'TDX', 'https://tdx.transportdata.tw/api/basic/v2/Bus/Vehicle?%24top=30&%24format=JSON');