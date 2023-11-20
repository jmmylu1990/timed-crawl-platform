package com.example.quartz.config;

import com.example.quartz.componet.AwareJobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableScheduling
public class QuartzConfig {

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /*
     * 通過SchedulerFactoryBean獲取Scheduler的實例
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            AwareJobFactory jobFactory,
            @Qualifier("quartzDataSource") DataSource dataSource,
            @Qualifier("quartzTransactionManager") PlatformTransactionManager quartzTransactionManager
    ) throws Exception {

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // 用於quartz集群, QuartzScheduler啟動時更新己存在的Job，這樣就不用每次刪除已存在的job記錄
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        // QuartzScheduler延時啟動，應用啟動完1秒後QuartzScheduler再啟動
//		factory.setStartupDelay(1);
        // 替換從spring創建實例使得spring注入正常運行
        factory.setJobFactory(jobFactory);
        // 配置自訂quartz.application.properties
        factory.setQuartzProperties(quartzProperties());
        //jdbc store
        factory.setDataSource(dataSource);
        factory.setTransactionManager(quartzTransactionManager);
        return factory;
    }
}
