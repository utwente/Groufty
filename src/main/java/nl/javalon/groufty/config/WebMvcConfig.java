package nl.javalon.groufty.config;

import lombok.extern.slf4j.Slf4j;
import nl.javalon.groufty.security.DevAlwaysLoggedInInterceptor;
import nl.javalon.groufty.security.GrouftyMethodSecurityExpressionRootFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Lukas Miedema
 */
@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Autowired(required = false) private DevAlwaysLoggedInInterceptor alwaysLoggedInInterceptor;
	@Inject private GrouftyMethodSecurityExpressionRootFactory expressionRootFactory;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(expressionRootFactory);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if (alwaysLoggedInInterceptor != null && alwaysLoggedInInterceptor.isEnabled()) {
			log.warn("Enabling always logged in user interceptor");
			registry.addInterceptor(alwaysLoggedInInterceptor);
		}
	}
}
