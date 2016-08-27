package nl.javalon.groufty.domain.user;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Lukas Miedema
 */
public enum UserAuthority implements GrantedAuthority {

	/**
	 * The user is a participant in the system. Participants can submit submissions and write reviews.
	 */
	ROLE_PARTICIPANT,

	/**
	 * The user is an editor in the system. Editors can create tasks, add users and modify permissions.
	 */
	ROLE_EDITOR,

	/**
	 * The user has no role in the system, and will not be allowed to access anything.
	 */
	ROLE_NONE;

	@Override
	public String getAuthority() {
		return name();
	}
}
