import com.s3.common.config.CommonSecurityConfig;
import com.s3.event.config.S3EventProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@OpenAPIDefinition(info = @Info(title = "Event Service", version = "v1"))
@SpringBootApplication(scanBasePackages = {"com.s3.event"})
@EnableConfigurationProperties(S3EventProperties.class)
//@Import(CommonSecurityConfig.class)
public class EventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }
}