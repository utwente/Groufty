package nl.javalon.groufty.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.javalon.groufty.Application;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserAuthority;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.dto.user.UserCredentialsDto;
import nl.javalon.groufty.repository.crud.UserRepository;
import nl.javalon.groufty.security.DevAuthenticationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
@ActiveProfiles("test")
@Transactional
public class UserSessionTest {

	private final static String SESSION_ENDPOINT = RestPrefixConfiguration.FULL_URL + "users/session";

	@Inject
	private WebApplicationContext webApplicationContext;

	@Inject
	private UserRepository userRepository;

	@Inject
	private ObjectMapper objectMapper;
	private MockMvc mockMvc;

	private User employee;

	@Before
	public void setUp() {

		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.apply(springSecurity()).build();

		employee = new User();
		employee.setFullName("Joel Jenkins");
		employee.setUserId(UserId.fromString("m325833"));
		employee.setAuthority(UserAuthority.ROLE_EDITOR);

		// Add the users by simply adding them to the DAO
		employee = userRepository.save(employee);
	}

	@Test
	public void testLogin() throws Exception {
		UserCredentialsDto userCredentialsDto = new UserCredentialsDto();

		userCredentialsDto.setUserId(employee.getUserId().toString());
		userCredentialsDto.setPassword(DevAuthenticationProvider.DEV_PASSWORD);

		mockMvc.perform(put(SESSION_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userCredentialsDto)))
				.andExpect(status().is2xxSuccessful());

		userCredentialsDto.setPassword("");

		mockMvc.perform(put(SESSION_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userCredentialsDto)))
				.andExpect(status().is4xxClientError());

		userCredentialsDto.setUserId("");
		userCredentialsDto.setPassword(DevAuthenticationProvider.DEV_PASSWORD);

		mockMvc.perform(put(SESSION_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userCredentialsDto)))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testLoginWithFalseCredentials() throws Exception {
		UserCredentialsDto userCredentialsDto = new UserCredentialsDto();

		userCredentialsDto.setUserId("s13164");
		userCredentialsDto.setPassword(DevAuthenticationProvider.DEV_PASSWORD);

		mockMvc.perform(put(SESSION_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userCredentialsDto)))
				.andExpect(status().is4xxClientError());

		userCredentialsDto.setUserId(employee.getUserId().toString());
		userCredentialsDto.setPassword("Wrong password");

		mockMvc.perform(put(SESSION_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userCredentialsDto)))
				.andExpect(status().is4xxClientError());
	}


	public void testLogout() throws Exception {
		UserCredentialsDto userCredentialsDto = new UserCredentialsDto();

		userCredentialsDto.setUserId(employee.getUserId().toString());
		userCredentialsDto.setPassword(DevAuthenticationProvider.DEV_PASSWORD);


		mockMvc.perform(put(SESSION_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userCredentialsDto)))
				.andExpect(status().is2xxSuccessful());

		mockMvc.perform(delete(SESSION_ENDPOINT))
				.andExpect(status().is2xxSuccessful());

	}


}
