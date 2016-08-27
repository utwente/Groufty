package nl.javalon.groufty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point of our Spring Boot application
 *
 */
@EnableConfigurationProperties
@EnableScheduling
@SpringBootApplication
@Configuration
public class Application {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);

		// And run
		app.run(args);
	}
}