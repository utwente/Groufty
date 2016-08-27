package nl.javalon.groufty.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.javalon.groufty.Application;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.task.*;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserAuthority;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.repository.crud.TaskListRepository;
import nl.javalon.groufty.repository.crud.TaskRepository;
import nl.javalon.groufty.repository.crud.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
@Transactional
@ActiveProfiles("test")
public class TaskListResourceTest {
	private final static String TASKLIST_ENDPOINT = RestPrefixConfiguration.FULL_URL + "tasklists/";

	@Inject private UserRepository userRepository;
	@Inject	private TaskListRepository taskListRepository;
	@Inject	private TaskRepository taskRepository;
	@Inject	private WebApplicationContext webApplicationContext;
	@Inject	private ObjectMapper objectMapper;

	private User userOne;
	private RequestPostProcessor employeeOneAuth;
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		// Create and store author
		userOne = new User();
		userOne.setUserId(UserId.fromString("m584095874"));
		userOne.setFullName("Diana Gray");
		userOne.setAuthority(UserAuthority.ROLE_EDITOR);
		userOne = userRepository.save(userOne);

		// Create auth
		employeeOneAuth = authentication(
				new UsernamePasswordAuthenticationToken(userOne, null, userOne.getAuthorities()));

		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.defaultRequest(get(TASKLIST_ENDPOINT).with(employeeOneAuth))
				.apply(springSecurity()).build();
	}

	@Test
	public void testGetTaskList() throws Exception {

		// Create list and save via the dao
		TaskList list = new TaskList();
		list.setSubmissionAudience(Audience.GROUP);
		list.setName("Cool task list");
		list.setAuthor(userOne);
		list.setSubmissionDeadline(new Date());
		list = taskListRepository.save(list);

		// Do a GET to the API
		mockMvc.perform(get(TASKLIST_ENDPOINT + list.getTaskListId()))
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().json(objectMapper.writeValueAsString(list)));
	}

	@Test
	public void testPostTaskList() throws Exception {

		// Create list
		TaskList list = new TaskList();
		list.setSubmissionAudience(Audience.GROUP);
		list.setName("New list");
		list.setAuthor(userOne);
		list.setSubmissionDeadline(new Date(System.currentTimeMillis() + 1000000));
		list.setState(TaskListState.DRAFT);

		String json = objectMapper.writeValueAsString(list);

		// POST list to the API
		mockMvc.perform(post(TASKLIST_ENDPOINT)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.taskListId").exists()); // the db will have created a primary key
	}

	@Test
	public void testDeleteTasklist() throws Exception {

		// Create a list via the dao
		TaskList list = new TaskList();
		list.setSubmissionAudience(Audience.GROUP);
		list.setAuthor(userOne);
		list.setSubmissionDeadline(new Date());
		list.setName("Doomed list");
		list = taskListRepository.save(list);

		System.out.println(objectMapper.writeValueAsString(list));

		// Create a child task. This delete should cascade
		Task child = new Task();
		child.setContentType(SubmissionContentType.PDF_SUBMISSION);
		child.setName("Doomed task");
		child.setAuthor(userOne);
		child.setTaskList(list);
		child = taskRepository.save(child);

		// DELETE list via the API
		mockMvc.perform(delete(TASKLIST_ENDPOINT + list.getTaskListId()))
				.andExpect(status().is2xxSuccessful());

		// Test if its deleted
		assertNull(taskListRepository.findOne(list.getTaskListId()));
	}

	@Test
	public void testUpdateTasklist() throws Exception {

		// Create a list via the dao
		TaskList initial = new TaskList();
		initial.setSubmissionAudience(Audience.GROUP);
		initial.setName("Initial list");
		initial.setState(TaskListState.DRAFT);
		initial.setAuthor(userOne);
		initial.setSubmissionDeadline(new Date());
		initial = taskListRepository.save(initial);

		// Prepare an update
		TaskList update = new TaskList();
		update.setSubmissionAudience(Audience.INDIVIDUAL);
		update.setName("Fixed list");
		update.setAuthor(userOne);
		update.setSubmissionDeadline(new Date());
		update.setState(TaskListState.DRAFT);

		// Perform PUT via the API
		System.out.println(mockMvc.perform(put(TASKLIST_ENDPOINT + initial.getTaskListId())
				.content(objectMapper.writeValueAsString(update))
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().is2xxSuccessful()));

		// Check if it really changed
		TaskList result = taskListRepository.findOne(initial.getTaskListId());
		assertEquals(update.getName(), result.getName());
		assertEquals(update.getSubmissionDeadline(), result.getSubmissionDeadline());
		assertEquals(update.getSubmissionAudience(), result.getSubmissionAudience());
	}
}
