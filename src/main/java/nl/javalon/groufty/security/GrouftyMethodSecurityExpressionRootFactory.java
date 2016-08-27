package nl.javalon.groufty.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.inject.Inject;

/**
 * Your one-stop shop for producing {@link GrouftyMethodSecurityExpressionRoot} instances.
 * Both a {@link HandlerMethodArgumentResolver} to allow injection of {@link GrouftyMethodSecurityExpressionRoot}
 * directly into controller methods permitting security in Java and a {@link MethodSecurityExpressionHandler} to set
 * the {@link GrouftyMethodSecurityExpressionRoot} as expression root for annotation-based security.
 * @author Lukas Miedema
 */
@Component
public class GrouftyMethodSecurityExpressionRootFactory extends AbstractSecurityExpressionHandler<MethodInvocation>
		implements MethodSecurityExpressionHandler, HandlerMethodArgumentResolver {

	@Inject
	private ApplicationContext context; // for dependency injection into the expression root

	// MethodSecurityExpressionHandler
	@Override
	public Object filter(Object filterTarget, Expression filterExpression, EvaluationContext ctx) {
		return null;
	}

	@Override
	public void setReturnObject(Object returnObject, EvaluationContext ctx) {}

	@Override
	protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
		// Create the actual expression root
		SecurityExpressionOperations operations = new GrouftyMethodSecurityExpressionRoot(authentication, invocation);
		context.getAutowireCapableBeanFactory().autowireBean(operations);
		return operations;
	}

	// HandlerMethodArgumentResolver
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType() == GrouftyMethodSecurityExpressionRoot.class;
	}

	@Override
	public Object resolveArgument(
			MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		// Delegate
		return createSecurityExpressionRoot(SecurityContextHolder.getContext().getAuthentication(), null);
	}
}
