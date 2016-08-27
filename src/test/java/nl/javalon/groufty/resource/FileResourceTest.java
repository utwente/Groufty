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
import nl.javalon.groufty.domain.util.FileDetails;
import nl.javalon.groufty.repository.crud.ReviewTemplateRepository;
import nl.javalon.groufty.repository.crud.TaskListRepository;
import nl.javalon.groufty.repository.crud.TaskRepository;
import nl.javalon.groufty.repository.crud.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.sql.rowset.serial.SerialBlob;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.fileUpload;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class FileResourceTest {

	private final static String BASE_URL = RestPrefixConfiguration.FULL_URL + "tasks/";
	private final static String TEST_FILE = "./testdata/uml.pdf";

	@Inject
	private TaskListRepository taskListRepository;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private ReviewTemplateRepository reviewTemplateRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private WebApplicationContext webApplicationContext;

	@Inject
	private ObjectMapper objectMapper;

	private User employeeOne;
	private TaskList tasklist;
	private FileDetails fileDetails;
	private byte[] rawFile;

	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		employeeOne = new User();
		employeeOne.setAuthority(UserAuthority.ROLE_EDITOR);
		employeeOne.setUserId(UserId.fromString("m1749635"));
		employeeOne.setFullName("Billy Jordan");
		employeeOne = userRepository.save(employeeOne);

		tasklist = new TaskList();
		tasklist.setSubmissionAudience(Audience.GROUP);
		tasklist.setAuthor(employeeOne);
		tasklist.setSubmissionDeadline(new Date());
		tasklist.setName("History Class");
		tasklist = taskListRepository.save(tasklist);

		rawFile = Files.readAllBytes(Paths.get(TEST_FILE));
		fileDetails = new FileDetails();
		Blob blob = new SerialBlob(rawFile);
		fileDetails.setFile(blob);
		fileDetails.setFileName("uml.pdf");

		// Create auth
		RequestPostProcessor employeeOneAuth = authentication(
				new UsernamePasswordAuthenticationToken(employeeOne, null, employeeOne.getAuthorities()));

		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.defaultRequest(get("").with(employeeOneAuth))
				.apply(springSecurity()).build();
	}

	@Test
	public void testGetFileOfTask() throws Exception {
		Task task = new Task();
		task.setName("A task with a file");
		task.setContentType(SubmissionContentType.TEXT_SUBMISSION);
		task.setAuthor(employeeOne);
		task.setTaskList(tasklist);
		task.setFileDetails(fileDetails);

		task = taskRepository.save(task);

		MvcResult mvcResult = mockMvc.perform(get(BASE_URL + task.getTaskId() + "/file"))
				.andExpect(status().is2xxSuccessful())
				.andReturn();
		byte[] content = mvcResult.getResponse().getContentAsByteArray();
		assertTrue(Arrays.equals(content, rawFile));
	}

	@Test
	public void testPostFileOfTask() throws Exception {

		// Create a task without file
		Task before = new Task();
		before.setName("Task with file");
		before.setAuthor(employeeOne);
		before.setContentType(SubmissionContentType.TEXT_SUBMISSION);
		before.setTaskList(tasklist);
		before = taskRepository.save(before);

		// Set the file via the API
		MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", "uml.pdf",
				MediaType.MULTIPART_FORM_DATA_VALUE, rawFile);
		mockMvc.perform(
				fileUpload(BASE_URL + before.getTaskId() + "/file")
						.file(multipartFile))
				.andExpect(status().is2xxSuccessful());

		// Check the file
		Task after = taskRepository.findOne(before.getTaskId());
		FileDetails newFileDetails = after.getFileDetails();
		Blob newFile = newFileDetails.getFile();
		assertEquals("uml.pdf", newFileDetails.getFileName());
		assertArrayEquals(rawFile, newFile.getBytes(1, (int) newFile.length()));
	}

	@Test
	public void testDeleteFileOfTask() throws Exception {

		// Create a task with a file via the DAO
		Task task = new Task();
		task.setName("Doomed task");
		task.setContentType(SubmissionContentType.PDF_SUBMISSION);
		task.setAuthor(employeeOne);
		task.setTaskList(tasklist);
		task.setFileDetails(fileDetails);
		task = taskRepository.save(task);

		// Delete via the API
		mockMvc.perform(delete(BASE_URL + task.getTaskId() + "/file")).andExpect(status().is2xxSuccessful());

		// Check if deleted
		assertNull(taskRepository.findOne(task.getTaskId()).getFileDetails());
	}
}
