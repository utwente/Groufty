package nl.javalon.groufty.security;

import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.repository.crud.UserRepository;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Maps a {@link DirContextOperations} from LDAP to a Groufty {@link User} domain object.
 * @author Lukas Miedema
 */
@Component
public class UtwenteUserContextMapper implements UserDetailsContextMapper {

	@Inject
	private UserRepository userRepository;

	/**
	 * Creates a fully populated UserDetails object for use by the security framework.
	 *
	 * @param ctx         the context object which contains the user information.
	 * @param username    the user's supplied login name.
	 * @param authorities
	 * @return the user object.
	 */
	@Override
	public UserDetails mapUserFromContext(
			DirContextOperations ctx,
			String username,
			Collection<? extends GrantedAuthority> authorities) {

		// Extract attributes
		String uid = ctx.getStringAttribute("uid");
		String givenName = ctx.getStringAttribute("givenname");
		String sn = ctx.getStringAttribute("sn");
		String fullName = givenName + " " + sn;

		// Parse username
		UserId userId = UserId.fromString(uid);

		// Search in db
		User user = userRepository.findOneById(userId);

		// Check existence
		if (user == null) {
			user = new User();
			user.setUserId(userId);
			user.setFullName(fullName);
			userRepository.save(user);
		} else {
			// Update with data from the server
			user.setFullName(fullName);
		}

		return user;
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		// ignore
	}
}
