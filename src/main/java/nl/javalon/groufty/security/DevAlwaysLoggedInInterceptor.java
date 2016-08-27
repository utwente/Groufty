package nl.javalon.groufty.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Development interceptor that treats every unauthenticated request
 * as logged by a certain user. This way, during development, you do not have to log in after every single
 * restart.
 * @author Lukas Miedema
 */
@Profile({"dev", "test"}) // explicitly prevent from being used in "prod"
@Slf4j
@Component
public class DevAlwaysLoggedInInterceptor extends HandlerInterceptorAdapter {

	@Inject private DevAuthenticationProvider devAuthenticationProvider;
	private String userId;

	@Inject
	private void setUserName(Environment environment) {
		this.userId = environment.getProperty("groufty.authentication.always-logged-in-user");
		log.info("User id: " + userId);
	}


	@Override
	public boolean preHandle(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			Object o) throws Exception {

		// Get current authentication
		SecurityContext ctx = SecurityContextHolder.getContext();
		if (ctx.getAuthentication() == null || ctx.getAuthentication() instanceof AnonymousAuthenticationToken) {

			// Authenticate
			UsernamePasswordAuthenticationToken token =
					new UsernamePasswordAuthenticationToken(userId, DevAuthenticationProvider.DEV_PASSWORD);
			Authentication auth = devAuthenticationProvider.authenticate(token);
			ctx.setAuthentication(auth);
		}

		return true;
	}

	/**
	 * @return true if this interceptor is enabled via the config, false otherwise
	 */
	public boolean isEnabled() {
		return userId != null && !userId.isEmpty();
	}
}
