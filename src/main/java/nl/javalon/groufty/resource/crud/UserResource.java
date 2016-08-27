package nl.javalon.groufty.resource.crud;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.dto.user.UserCredentialsDto;
import nl.javalon.groufty.repository.crud.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static nl.javalon.groufty.resource.RestResourceHelper.checkFound;

/**
 * REST endpoint for Users
 * 
 * @author Melcher
 *
 */
@Api(description = "Manage users")
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PREFIX + "users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserResource {

	@Inject
	private UserRepository userRepository;

	@Inject
	private AuthenticationManager authenticationManager;

	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@ApiOperation(value = "Create a new user", notes = "user must contain a userId")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public User post(@ApiParam(required = true) @Valid @RequestBody User user) {
		return userRepository.save(user);
	}

	@ApiOperation("Retrieve all users, paginated")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Page<User> getAll(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@ApiOperation("Retrieve a single user")
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public User get(@ApiParam(required = true) @PathVariable UserId userId) {
		return checkFound(userRepository.findOneById(userId));
	}

	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@ApiOperation(value = "Update an existing user")
	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public void put(@ApiParam(required = true) @PathVariable UserId userId,
	                   @ApiParam(required = true) @RequestBody User user) {
		User current = checkFound(userRepository.findOneById(userId));
		current.setFullName(user.getFullName());
	}

	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@ApiOperation(value = "Delete a user", notes = "Deletion will fail if the user has authored tasks/tasklists/submissions/reviews")
	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UserId userId) {
		userRepository.deleteById(userId);
	}


	// Session related
	@ApiOperation("Retrieve currently authenticated user")
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/session", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public User getSession(Authentication authentication) {
		return (User) authentication.getPrincipal();
	}

	@ApiOperation(value = "Logs in with the provided credentials",
			notes = "Principal is user id, i.e. s1234. Credentials is plaintext password.")
	@RequestMapping(value = "/session", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public User putSession(@Valid @RequestBody UserCredentialsDto credentials) {
		Authentication token =
				new UsernamePasswordAuthenticationToken(credentials.getUserId(), credentials.getPassword());
		Authentication auth = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return (User) auth.getPrincipal();
	}

	@ApiOperation(value = "Logs out")
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/session", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deleteSession() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}
