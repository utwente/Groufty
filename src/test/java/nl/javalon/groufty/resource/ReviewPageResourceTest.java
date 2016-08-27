package nl.javalon.groufty.resource;

import nl.javalon.groufty.Application;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.task.*;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserAuthority;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.repository.crud.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.sql.Date;
import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
@ActiveProfiles("test")
@Transactional
public class ReviewPageResourceTest {

	private final static String PAGE_ENDPOINT = RestPrefixConfiguration.FULL_URL + "page";
	private final static String REVIEW_PAGE_ENDPOINT = PAGE_ENDPOINT + "/review-overview";

	@Inject
	private WebApplicationContext webApplicationContext;

	@Inject
	private UserRepository userRepository;

	@Inject
	private TaskListRepository taskListRepository;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private ReviewRepository reviewRepository;

	@Inject
	private SubmissionRepository submissionRepository;

	@Inject
	private SubmissionListRepository submissionListRepository;

	private MockMvc mockMvc;
	private User studentOne;
	private RequestPostProcessor employeeOneAuthentication;
	private User studentTwo;
	private TaskList taskListOne;
	private Task task;
	private Review review;
	private Submission submission;
	private SubmissionList submissionList;

	private Date reviewDealine = new Date(Calendar.getInstance().getTime().getTime() + 1000000);
	private Date lastEdited = new Date(Calendar.getInstance().getTime().getTime());

	@Before
	public void setUp() {

		// Create fakes
		studentOne = new User();
		studentOne.setFullName("Neville Samson");
		studentOne.setUserId(UserId.fromString("s2685153"));
		studentOne.setAuthority(UserAuthority.ROLE_PARTICIPANT);
		studentOne = userRepository.save(studentOne);
		employeeOneAuthentication = authentication(
				new UsernamePasswordAuthenticationToken(studentOne, null, studentOne.getAuthorities()));

		studentTwo = new User();
		studentTwo.setFullName("John Johnson");
		studentTwo.setUserId(UserId.fromString("s1875249"));
		studentTwo.setAuthority(UserAuthority.ROLE_PARTICIPANT);
		studentTwo = userRepository.save(studentTwo);

		taskListOne = new TaskList();
		taskListOne.setName("Tasklist 1");
		taskListOne.setAuthor(studentOne);
		taskListOne.setSubmissionAudience(Audience.GROUP);
		taskListOne.setState(TaskListState.ACTIVE);
		taskListOne.setSubmissionDeadline(new Date(Calendar.getInstance().getTime().getTime() + 100000));
		taskListOne = taskListRepository.save(taskListOne);

		task = new Task();
		task.setContentType(SubmissionContentType.TEXT_SUBMISSION);
		task.setName("Add task");
		task.setAuthor(studentOne);
		task.setTaskList(taskListOne);
		task = taskRepository.save(task);

		SubmissionList.SubmissionListPk submissionListPk = new SubmissionList.SubmissionListPk();
		submissionListPk.setAuthor(studentOne);
		submissionListPk.setTaskList(taskListOne);

		submissionList = new SubmissionList();
		submissionList.setFinalized(true);
		submissionList.setId(submissionListPk);
		submissionListRepository.save(submissionList);


		Submission.SubmissionPk submissionPk = new Submission.SubmissionPk();
		submissionPk.setTask(task);
		submissionPk.setSubmissionList(submissionList);

		submission = new Submission();
		submission.setLastEdited(lastEdited);
		submission.setText("Description");
		submission.setSubmitted(true);
		submission.setId(submissionPk);
		submission = submissionRepository.save(submission);

		review = new Review();
		review.setAuthor(studentOne);
		review.setSubmitted(true);
		review.setLastEdited(lastEdited);
		review.setSubmission(submission);
		review = reviewRepository.save(review);

		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.defaultRequest(get(REVIEW_PAGE_ENDPOINT).with(employeeOneAuthentication))
				.build();
	}

	@Test
	public void getReviewOverview() throws Exception {
		// Insert the user via the DAO
		MvcResult result = mockMvc.perform(get(REVIEW_PAGE_ENDPOINT))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.first", is(true)))
				//.andExpect(jsonPath("$.content", Matchers.hasSize(1)))
				.andReturn();


		System.out.println(result.getResponse().getContentAsString());

	}
}
