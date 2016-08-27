package nl.javalon.groufty.security;

import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.repository.crud.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Provides authentication during development without accessing the LDAP server.
 * Ignores the password and lets you log in with the provided user id.
 * @author Lukas Miedema
 */
@Component
@Profile({"dev", "test"}) // explicitly prevent this from being used in "prod"
public class DevAuthenticationProvider implements AuthenticationProvider {

	public static final String DEV_PASSWORD = "DEMO";

	@Inject
	private UserRepository userRepository;

	@Override
	public Authentication authenticate(Authentication raw) throws AuthenticationException {
		UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) raw;
		String name = authentication.getName();
		String password = (String) authentication.getCredentials();

		if (!password.equals(DEV_PASSWORD)) {
			throw new BadCredentialsException("Wrong password");
		}

		UserId id = UserId.fromString(name);
		User user = userRepository.findOneById(id);
		if (user == null) {
			throw new UsernameNotFoundException(id + " could not be found");
		}

		// Construct auth depending on role
		Authentication out = new UsernamePasswordAuthenticationToken(user, "TEST/TEST/TEST", user.getAuthorities());
		return out;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication == UsernamePasswordAuthenticationToken.class;
	}
}
