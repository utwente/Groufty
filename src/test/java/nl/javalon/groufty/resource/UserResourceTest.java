package nl.javalon.groufty.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.javalon.groufty.Application;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserAuthority;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.repository.crud.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
@ActiveProfiles("test")
@Transactional
@Rollback
public class UserResourceTest {

	private final static String USER_ENDPOINT = RestPrefixConfiguration.FULL_URL + "users/";

	@Inject private WebApplicationContext webApplicationContext;
	@Inject	private UserRepository userRepository;

	@Inject	private ObjectMapper objectMapper;
	private MockMvc mockMvc;

	// Users and their authorities
	private User student;
	private User employee;
	private RequestPostProcessor studentAuthority;
	private RequestPostProcessor employeeAuthority;

	@Before
	public void setUp() {
		// Setup with security
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.build();

		student = new User();
		student.setAuthority(UserAuthority.ROLE_PARTICIPANT);
		student.setFullName("Peter Bennett");
		student.setUserId(UserId.fromString("s9023453"));
		studentAuthority = authentication(new UsernamePasswordAuthenticationToken(student, null, student.getAuthorities()));

		employee = new User();
		employee.setAuthority(UserAuthority.ROLE_EDITOR);
		employee.setFullName("Alex Morris");
		employee.setUserId(UserId.fromString("m325833"));
		employeeAuthority = authentication(new UsernamePasswordAuthenticationToken(employee, null, employee.getAuthorities()));

		// Add the users by simply adding them to the DAO
		student = userRepository.save(student);
		employee = userRepository.save(employee);
	}

	@Test
	public void testAddUser() throws Exception {
		User u = new User();
		u.setFullName("Bobby Jenkins");
		u.setUserId(UserId.fromString("s1545654"));
		String userJson = objectMapper.writeValueAsString(u);

		// Test adding the user as student, which should fail as students cant add users
		mockMvc.perform(post(USER_ENDPOINT)
				.with(studentAuthority)
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson))
				.andExpect(status().isForbidden());
		assertNull(userRepository.findOneById(u.getUserId()));

		// Test adding the user as employee, which should succeed
		mockMvc.perform(post(USER_ENDPOINT)
				.with(employeeAuthority)
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson))
				.andExpect(status().is2xxSuccessful());

		User savedUser = userRepository.findOneById(u.getUserId());
		assertNotNull(savedUser);
		assertEquals(u.getFullName(), savedUser.getFullName());
		assertEquals(u.getUserId(), savedUser.getUserId());
		assertEquals(u.getUserId(), savedUser.getUserId());
	}

	@Test
	public void testGetUser() throws Exception{
		User u1 = new User();
		String u1Name = "Eleanor Bennett";
		u1.setFullName(u1Name);
		u1.setUserId(UserId.fromString("s1345604"));

		User u2 = new User();
		String u2Name = "Gabriel Morris";
		u2.setFullName(u2Name);
		u2.setUserId(UserId.fromString("s1450088"));

		// Add the users by simply adding them to the DAO
		u1 = userRepository.save(u1);
		u2 = userRepository.save(u2);

		mockMvc.perform(get(USER_ENDPOINT + u1.getUserId()).with(studentAuthority))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.fullName", is(u1Name)))
				.andExpect(jsonPath("$.userId", is(u1.getUserId().toString())));

		// And we do the same for the second user
		mockMvc.perform(get(USER_ENDPOINT + u2.getUserId()).with(employeeAuthority))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.fullName", is(u2Name)))
				.andExpect(jsonPath("$.userId", is(u2.getUserId().toString())));
	}

	@Test
	public void testUpdateUser() throws Exception {
		User before = new User();
		String name = "Diane McCartney";
		before.setFullName(name);
		before.setUserId(UserId.fromString("s1444054"));

		// Store the before via the DAO
		before = userRepository.save(before);

		// Prepare an update for the before
		// We cannot modify the before object because it has become a managed entity now,
		// making it lead to false positives
		User update = new User();
		String updatedName = "Jesse Coleman";
		update.setFullName(updatedName);

		// Perform the update
		mockMvc.perform(put(USER_ENDPOINT + before.getUserId()).with(employeeAuthority)
				.content(objectMapper.writeValueAsString(update))
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().is2xxSuccessful());

		// Check by retrieving
		User after = userRepository.findOne(before.getAuthorId());
		assertEquals(updatedName, after.getFullName());
	}

	@Test
	public void testDeleteUser() throws Exception {
		User u = new User();
		u.setFullName("Emilie Boyd");
		u.setUserId(UserId.fromString("s1671798"));

		// Insert the user via the DAO
		userRepository.save(u);

		// Delete via DELETE
		mockMvc.perform(delete(RestPrefixConfiguration.FULL_URL + "users/" + u.getUserId()).with(employeeAuthority))
				.andExpect(status().is(204));
	}
}
