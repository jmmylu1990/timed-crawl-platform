1.執行batch的批次api:http://localhost:8081/batch/api/download?jobName=cityAndInterCityBusJob
</br>
2.安裝排程Job進入Quartz:http://localhost:8082/quartz/api/init
</br>
3.激活Quartz裡面的市區公車車流排程:http://localhost:8082/quartz/api/execute/123