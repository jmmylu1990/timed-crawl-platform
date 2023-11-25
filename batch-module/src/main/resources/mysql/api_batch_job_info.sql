-- customizedbatch.api_batch_job_info definition
DROP TABLE IF EXISTS api_batch_job_info;
CREATE TABLE `api_batch_job_info` (
  `execute_name` varchar(255) NOT NULL,
  `job_type` varchar(255) NOT NULL,
  `data_format` varchar(255) NOT NULL,
  `class_name` varchar(255) NOT NULL,
  `chunk_size` INT NOT NULL,
  `resource_url` varchar(255) NOT NULL,
  PRIMARY KEY (`execute_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO customizedbatch.api_batch_job_info
(execute_name, job_type, data_format, class_name, chunk_size, resource_url)
VALUES('cityAndInterCityBusJob', 'TDX', 'JSON', 'CityAndInterCityBus', 10, 'https://tdx.transportdata.tw/api/basic/v2/Bus/Vehicle?%24top=30&%24format=JSON');