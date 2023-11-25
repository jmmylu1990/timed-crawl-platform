This side project is a concept, focusing on redeveloping the previous company's scheduled for updates of transportation data.
</br>
Requesting the Ministry of Transportation's API and providing users with query capabilities.
</br>
As the previous company's project did not adopt batch processing, microservices and distributed architecture,
</br>
I am currently planning the architecture for the first version.
</br>
</br>
This side project was designed in just a few days, and there are still many areas that require further refinement.
</br>
</br>
1.Using Spring Quartz to schedule the execution of an API at regular intervals.
</br>
</br>
2.Using Spring Batch for batch processing of large volumes of data.
</br>
Batch-module's API use the Ministry of Transportation's TDX service, and obtaining a TDX'key is required. If you prefer not to use this service, please replace the content of batch-module.
</br>
</br>
3.Implementing high availability by using Redis master-slave replication and sentinel.
</br>
</br>
4.Using Redisson's distributed lock to accurately calculate the usage count of the API.
</br>
</br>
5.Using Spring Batch to enqueue AOP requests to Redis. (Modifyed the content in the fourth point.)
</br>
</br>
6.Using Ministry of Transportation's API, It's need TDX token,
</br>
</br>
Execute the batch API: http://localhost:8081/batch/api/download?jobName=cityAndInterCityBusJob
</br>
Install the scheduled job into Quartz: http://localhost:8082/quartz/api/init
</br>
Activate the "cityAndInterCityBusJob" schedule within Quartz: http://localhost:8082/quartz/api/execute/123