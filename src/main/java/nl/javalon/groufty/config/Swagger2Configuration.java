package nl.javalon.groufty.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * @author Lukas Miedema
 */
@EnableSwagger2
@Configuration
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Configuration {

	@Bean
	public Docket grouftyApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.paths(regex("/api/v1/.*"))
				.build();
	}
}
