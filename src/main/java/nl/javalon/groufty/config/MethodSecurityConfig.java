package nl.javalon.groufty.config;

import lombok.extern.slf4j.Slf4j;
import nl.javalon.groufty.security.GrouftyMethodSecurityExpressionRootFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import javax.inject.Inject;

/**
 * Sets {@link GrouftyMethodSecurityExpressionRootFactory} as {@link MethodSecurityExpressionHandler} and enables
 * method security.
 * @author Lukas Miedema
 */
@Slf4j
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Inject private GrouftyMethodSecurityExpressionRootFactory expressionRootFactory;

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		return expressionRootFactory;
	}
}