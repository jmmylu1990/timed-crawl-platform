package com.example.customer.aspect;

import com.example.customer.model.ApiLog;
import com.example.customer.service.LogMessageService;
import com.example.customer.utils.DateUtils;
import com.example.customer.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@EnableAspectJAutoProxy(exposeProxy = true)
public class UsageCountServiceAspect {

    @Value("${kafka.topic.apilog-topic}")
    private String apiLogTopic;
    @Autowired
    private LogMessageService logMessageService;
    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("execution(* com.example.customer.controller..*(..)) && args(.., request)")
    public void controllerLayer(HttpServletRequest request) {
    }

    @After("controllerLayer(request)")
    public void afterControllerMethod(JoinPoint joinPoint,HttpServletRequest request) throws IOException {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        HttpUtils.getIpAddr(request);
        String realIpAddress = (xForwardedForHeader != null) ? xForwardedForHeader.split(",")[0] : request.getRemoteAddr();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        String methodName = methodSignature.getName();

        java.lang.reflect.Method method = methodSignature.getMethod();
        Class<?> declaringClass = method.getDeclaringClass();
        String clazzSimpleName = declaringClass.getSimpleName();
        String packageName = declaringClass.getPackage().getName();
        Date executeDateTime = DateUtils.parseStrToDate(DateUtils.DASHED_DATETIME_FORMAT,DateUtils.now());

        ApiLog apiLog = new ApiLog(realIpAddress,methodName,clazzSimpleName,packageName,executeDateTime);

        logMessageService.sendMessage(apiLogTopic,apiLog);
    }



//    @After("controllerLayer()")
//    public void afterControllerMethod(JoinPoint joinPoint) throws IOException {
//        // 在分散式環境下獲取分布式鎖
//        RLock lock = redissonClient.getLock("myLock");
//        try {
//            // 嘗試獲取鎖，最多等待10秒
//            boolean isLockAcquired = lock.tryLock(10, TimeUnit.SECONDS);
//
//            if (isLockAcquired) {
//                MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//                String methodName = methodSignature.getName();
//                RLongAdder counter = redissonClient.getLongAdder("controllerMethodCount:" + methodName);
//                counter.increment();
//                RBucket<String> bucket = redissonClient.getBucket("controllerMethodCount:" + methodName);
//                bucket.set(String.valueOf(counter.sum()));
//                log.info("Lock acquired successfully!");
//            } else {
//                // 未能在指定時間內獲取到鎖
//                log.warn("Failed to acquire lock within 10 seconds.");
//            }
//        } catch (InterruptedException e) {
//            // 處理中斷異常
//            Thread.currentThread().interrupt();
//            log.error("Failed to acquire lock within 10 seconds.");
//        } finally {
//            // 無論是否成功獲取到鎖，都需要在最終釋放鎖
//            lock.unlock();
//        }
//    }
}
