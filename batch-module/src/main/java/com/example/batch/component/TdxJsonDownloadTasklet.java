package com.example.batch.component;

import com.example.batch.service.TdxService;
import com.example.batch.utils.HttpUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Component
public class TdxJsonDownloadTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TdxJsonDownloadTasklet.class);

    @Autowired
    TdxService tdxService;


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
        String token = tdxService.getToken();
        Map<String, String> headers = HttpUtils.customHeaderFoTDX(token);
        String resourceURL = (String)jobParameters.get("resourceURL");
        String batchDownloadPath = (String)jobParameters.get("filePath");
        String jobFileName = (String)jobParameters.get("jobFileName");
        File file = new File(batchDownloadPath);
        if (!file.exists()){
            file.mkdirs();
        }

        Map<String, String> fileInfoMap = new HashMap<>();
        fileInfoMap.put("methodType", "GET");
        fileInfoMap.put("outputFilePath", batchDownloadPath);
        fileInfoMap.put("outputFileName", jobFileName);
        tdxContentDownload(resourceURL, fileInfoMap, headers);

        return RepeatStatus.FINISHED;
    }

    private boolean tdxContentDownload(String url, Map<String, String> fileInfoMap, Map<String, String> headers) throws Exception {
        headers = Optional.ofNullable(headers).orElseGet(HashMap::new);
        fileInfoMap = Optional.ofNullable(fileInfoMap).orElseGet(HashMap::new);
        String methodType = fileInfoMap.get("methodType");
        String outputFilePath = fileInfoMap.get("outputFilePath");
        String outputFileName = fileInfoMap.get("outputFileName");
        if (!headers.containsKey(HttpHeaders.USER_AGENT)) {
            headers.put(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Linux; Android 4.2.1; Nexus 7 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19");
        }
        File outputFile = null;
        try (CloseableHttpClient httpClient = (CloseableHttpClient) HttpUtils.createHttpClient();
             CloseableHttpResponse response = httpClient.execute(HttpUtils.toHttpRequest(url, methodType, headers))) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                outputFile = new File(outputFilePath, outputFileName);
                File parentFile = outputFile.getParentFile();
                if (!parentFile.exists()) parentFile.mkdirs();
                try (BufferedInputStream in = new BufferedInputStream(response.getEntity().getContent());
                     BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                    byte[] bytes = new byte[8192];
                    int size;
                    while ((size = in.read(bytes)) != -1) {
                        out.write(bytes, 0, size);
                    }
                    out.flush();
                }
            } else {
                throw new IOException("The URL is not available so that the connection failed.");
            }

        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}