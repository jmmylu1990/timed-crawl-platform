import com.example.customer.CustomerModuleApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= CustomerModuleApplication.class)
public class CustomerModuleApplicationTest {

    private final String url1 = "http://localhost:8083/customer/api/query/queryCityAndInterCityBusList";
    private final String url2 = "http://localhost:8084/customer/api/query/queryCityAndInterCityBusList";
    private final int concurrentThreads = 20;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testConcurrentRequests() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);
        CountDownLatch latch = new CountDownLatch(concurrentThreads * 2);

        // Concurrent requests to URL1
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                restTemplate.getForObject(url1, String.class);
                latch.countDown();
            });
        }

        // Concurrent requests to URL2
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                restTemplate.getForObject(url2, String.class);
                latch.countDown();
            });
        }

        // Wait for all requests to complete
        latch.await();

        // Shutdown the executor service
        executorService.shutdown();
    }
}
