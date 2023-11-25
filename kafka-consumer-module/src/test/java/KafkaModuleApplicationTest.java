import com.example.kafka.KafkaModuleApplication;
import com.example.kafka.service.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= KafkaModuleApplication.class)
public class KafkaModuleApplicationTest {

    @Autowired
    private RedisService redisService;


    @Test
    public void test(){
        List<Object> objectList = redisService.getAllFromList("apiLog");

        List<Object> flattenedList = objectList.stream()
                .filter(item -> item instanceof List)
                .flatMap(item -> ((List<Object>) item).stream())
                .toList();

      //  flattenedList.forEach(System.out::println);

        System.out.println("flattenedList.size():"+flattenedList.size());
    }
}
