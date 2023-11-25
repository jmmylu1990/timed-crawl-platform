import com.example.customer.CustomerModuleApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= CustomerModuleApplication.class)
public class CustomerModuleApplicationTest {

    private final String url1 = "http://localhost:8083/customer/api/query/queryCityAndInterCityBusList";
    private final String url2 = "http://localhost:8084/customer/api/query/queryCityAndInterCityBusList";
    private final int concurrentThreads = 100;

    private final RestTemplate restTemplate = new RestTemplate();
    @Test
    public void testConcurrentRequests() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads * 2);
        CountDownLatch latch = new CountDownLatch(concurrentThreads * 2);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Concurrent requests to URL1
        for (int i = 0; i < concurrentThreads; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(1000);
                    restTemplate.getForObject(url1, String.class);
                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }, executorService));
        }

        // Concurrent requests to URL2
        for (int i = 0; i < concurrentThreads; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(1000);
                    restTemplate.getForObject(url2, String.class);
                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, executorService));
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Shutdown the executor service
        executorService.shutdown();
    }

    public void ConcurrentRequestTest() {
        // Configure a connection pool for the RestTemplate
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(5000); // 5 seconds
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(5000); // 5 seconds
    }



}
