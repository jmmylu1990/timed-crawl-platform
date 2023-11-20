package com.example.batch.config;

import com.example.batch.component.ParamaterListener;
import com.example.batch.job.listener.JobCompletionNotificationListener;
import com.example.batch.job.model.CityAndInterCityBus;
import com.example.batch.job.model.base.JsonJobModel;
import com.example.batch.mapper.CityAndInterCityBusReaderJsonReader;
import com.example.batch.policy.ItemReaderSkipPolicy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.NonSkippableReadException;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class JsonBatchJobConfig<T extends JsonJobModel> {


    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private Tasklet tdxJsonDownloadTasklet;

    @Autowired
    private ParamaterListener paramaterListener;

    private static final String WILL_BE_INJECTED = null;

    @Bean
    public Job cityAndInterCityBusJob(JobCompletionNotificationListener listener, Step tdxJsonDownloadTaskletStep, Step cityAndInterCityBusStep1) {

        return new JobBuilder("cityAndInterCityBusJob")
                .repository(jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(tdxJsonDownloadTaskletStep)
                .next(cityAndInterCityBusStep1)
                .build();
    }


    @Bean
    public Step tdxJsonDownloadTaskletStep(PlatformTransactionManager transactionManager) throws BindException {
        return new StepBuilder("tdxJsonDownloadTasklet")
                .tasklet(tdxJsonDownloadTasklet)
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .build();
    }
    @Bean
    public Step cityAndInterCityBusStep1(PlatformTransactionManager transactionManager, JpaItemWriter<CityAndInterCityBus> cityAndInterCityBusWriter, ItemReader<CityAndInterCityBus> cityAndInterCityBusReader) throws BindException {
        return new StepBuilder("cityAndInterCityBusStep1")
                .repository(jobRepository)
                .transactionManager(transactionManager)
                //批次處理的物件
                .<CityAndInterCityBus, CityAndInterCityBus>chunk(10)
                .reader(cityAndInterCityBusReader)
                .processor(cityAndInterCityBusProcessor())
                .writer(cityAndInterCityBusWriter)
                .faultTolerant()  // 啟用容錯處理
                .skipPolicy(new ItemReaderSkipPolicy())
                //.skip(FlatFileParseException.class)  // 遇到 FlatFileParseException 類型的異常跳過
                .skipLimit(10)  // 最多跳過10次
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<CityAndInterCityBus> cityAndInterCityBusReader(@Value("#{jobParameters['jobFileName']}") String jobFileName,
                                                                         @Value("#{jobParameters['filePath']}") String filePath) throws IOException {

        return new JsonItemReaderBuilder<CityAndInterCityBus>()
                .name("cityAndInterCityBusReader")
                .resource(new FileSystemResource(filePath + jobFileName))
                .jsonObjectReader(new JacksonJsonObjectReader<>(CityAndInterCityBus.class))
                .build();

    }

    @Bean
    public ItemProcessor<CityAndInterCityBus, CityAndInterCityBus> cityAndInterCityBusProcessor() {
        return cityAndInterCityBus -> cityAndInterCityBus; // 簡單的返回原始物件
    }

    @Bean
    public JpaItemWriter<CityAndInterCityBus> cityAndInterCityBusWriter(DataSource dataSource, EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<CityAndInterCityBus> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }


}
