package nl.javalon.groufty.security;

import nl.javalon.groufty.domain.user.Author;
import nl.javalon.groufty.domain.user.Group;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.repository.crud.GroupRepository;
import nl.javalon.groufty.repository.crud.GroupingRepository;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

import javax.inject.Inject;

/**
 * @author Lukas Miedema
 */
public class GrouftyMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

	@Inject private GroupRepository groupRepository;
	@Inject private GroupingRepository groupingRepository;

	private Object filterObject;
	private Object returnObject;
	private Object target;

	@Override
	public void setFilterObject(Object filterObject) {
		this.filterObject = filterObject;
	}

	@Override
	public Object getFilterObject() {
		return filterObject;
	}

	@Override
	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	@Override
	public Object getReturnObject() {
		return returnObject;
	}

	@Override
	public Object getThis() {
		return target;
	}

	/**
	 * Creates a new instance
	 *
	 * @param authentication the {@link Authentication} to use. Cannot be null.
	 */
	public GrouftyMethodSecurityExpressionRoot(Authentication authentication, Object target) {
		super(authentication);
		setTrustResolver(new AuthenticationTrustResolverImpl());
		this.target = target;
	}

	/**
	 * Shortcut for {@link #inGroup(long)}
	 * @param author either a {@link nl.javalon.groufty.domain.user.User} or a {@link Group}
	 * @return true if the current authenticated user is
	 */
	public boolean inGroup(Author author) {
		return inGroup(author.getAuthorId());
	}

	/**
	 * Tests if the currently authenticated user is or is part of this author group.
	 * @param authorId
	 * @return
	 */
	public boolean inGroup(long authorId) {
		long userAuthorId = ((User) getAuthentication().getPrincipal()).getAuthorId();
		return authorId == userAuthorId || groupRepository.groupContains(userAuthorId, authorId);
	}

	/**
	 * Tests if the currently authenticated user is part of the provided grouping
	 * @param groupingId
	 * @return
	 */
	public boolean inGrouping(long groupingId) {
		long userAuthorId = ((User) getAuthentication().getPrincipal()).getAuthorId();
		return groupingRepository.groupingContains(userAuthorId, groupingId);
	}
}
