package nl.javalon.groufty.resource.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.dto.page.task.StudentTaskListOverviewDto;
import nl.javalon.groufty.repository.page.StudentFeedbackPageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Everything "My Feedback".
 * @author Lukas Miedema
 */
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PAGE_PREFIX + "student")
@Api("Everything 'My Feedback'")
public class StudentFeedbackPageResource {

	@Inject
	private StudentFeedbackPageRepository studentFeedbackPageRepository;

	@ApiOperation("Get all task lists assigned to the current user, with a submission deadline in the past")
	@RequestMapping(value = "feedback-overview", method = RequestMethod.GET)
	public Page<StudentTaskListOverviewDto> getFeedbackOverview(Authentication authentication, Pageable page) {
		return studentFeedbackPageRepository.getStudentFeedbackOverview(((User) authentication.getPrincipal()).getAuthorId(), page);
	}
}
