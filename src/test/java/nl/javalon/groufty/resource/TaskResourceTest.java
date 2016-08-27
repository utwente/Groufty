package nl.javalon.groufty.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.javalon.groufty.Application;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.task.Audience;
import nl.javalon.groufty.domain.task.SubmissionContentType;
import nl.javalon.groufty.domain.task.Task;
import nl.javalon.groufty.domain.task.TaskList;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserAuthority;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.repository.crud.ReviewTemplateRepository;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
@ActiveProfiles("test")
@Transactional
public class TaskResourceTest {
	private final static String TASK_ENDPOINT = RestPrefixConfiguration.FULL_URL + "tasks/";

	@Inject
	private WebApplicationContext webApplicationContext;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private TaskListRepository taskListRepository;

	@Inject
	private ReviewTemplateRepository reviewTemplateRepository;

	@Inject
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;
	private User employeeOne;
	private RequestPostProcessor employeeOneAuthentication;
	private User employeeTwo;
	private TaskList taskListOne;

	@Before
	public void setUp() {
		// Create fakes
		employeeOne = new User();
		employeeOne.setFullName("Neville Samson");
		employeeOne.setUserId(UserId.fromString("m2685153"));
		employeeOne.setAuthority(UserAuthority.ROLE_EDITOR);
		employeeOne = userRepository.save(employeeOne);
		employeeOneAuthentication = authentication(
				new UsernamePasswordAuthenticationToken(employeeOne, null, employeeOne.getAuthorities()));

		employeeTwo = new User();
		employeeTwo.setFullName("John Johnson");
		employeeTwo.setUserId(UserId.fromString("m1875249"));
		employeeTwo = userRepository.save(employeeTwo);

		taskListOne = new TaskList();
		taskListOne.setName("Tasklist 1");
		taskListOne.setAuthor(employeeOne);
		taskListOne.setSubmissionAudience(Audience.GROUP);
		taskListOne = taskListRepository.save(taskListOne);

		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.defaultRequest(get(TASK_ENDPOINT).with(employeeOneAuthentication))
				.build();
	}

	@Test
	public void testPostTask() throws Exception {

		Task task = new Task();
		task.setContentType(SubmissionContentType.TEXT_SUBMISSION);
		task.setName("Add task");
		task.setAuthor(employeeOne);
		task.setTaskList(taskListOne);

		String taskString = objectMapper.writeValueAsString(task);

		// Insert the user via the DAO
		mockMvc.perform(post(TASK_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(taskString))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testGetTask() throws Exception{

		// Create a task via the DAO
		Task task = new Task();
		task.setName("Get task");
		task.setContentType(SubmissionContentType.PDF_SUBMISSION);
		task.setAuthor(employeeOne);
		task.setTaskList(taskListOne);
		task = taskRepository.save(task);

		// Get all tasks (list of all tasks in the system)
		mockMvc.perform(get(TASK_ENDPOINT))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

		// Get this one task in particular
		mockMvc.perform(get(TASK_ENDPOINT + task.getTaskId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().json(objectMapper.writeValueAsString(task)));
	}

	@Test
	public void testUpdateTask() throws Exception {
		Task taskBefore = new Task();
		taskBefore.setContentType(SubmissionContentType.PDF_SUBMISSION);
		taskBefore.setName("Name before");
		taskBefore.setAuthor(employeeOne);
		taskBefore.setTaskList(taskListOne);
		taskBefore = taskRepository.save(taskBefore);

		Task taskUpdated = taskRepository.save(taskBefore);
		taskUpdated.setName("Name after");
		taskUpdated.setDescription("A very nice task after this update");
		taskUpdated.setAuthor(employeeTwo);
		taskUpdated.setTaskList(taskListOne);

		// Perform the update via the REST api
		mockMvc.perform(put(TASK_ENDPOINT + taskBefore.getTaskId())
				.content(objectMapper.writeValueAsString(taskUpdated))
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().is2xxSuccessful());

		Task taskAfter = taskRepository.findOne(taskBefore.getTaskId());
		assertEquals(taskUpdated.getAuthorId(),
				taskAfter.getAuthorId());
		assertEquals(taskUpdated.getTaskListId(), taskAfter.getTaskListId());
		assertEquals(taskUpdated.getName(), taskAfter.getName());
		assertEquals(taskUpdated.getDescription(), taskAfter.getDescription());
	}

	@Test
	public void testDeleteTask() throws Exception {
		Task task = new Task();
		task.setName("Doomed task");
		task.setContentType(SubmissionContentType.TEXT_SUBMISSION);
		task.setAuthor(employeeOne);
		task.setTaskList(taskListOne);
		task = taskRepository.save(task);

		mockMvc.perform(delete(TASK_ENDPOINT + task.getTaskId()))
				.andExpect(status().is(204));

		assertNull(taskRepository.findOne(task.getTaskId()));
	}


}
