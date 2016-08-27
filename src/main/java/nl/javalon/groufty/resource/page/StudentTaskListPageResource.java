package nl.javalon.groufty.resource.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.dto.page.task.StudentTaskListExpandDto;
import nl.javalon.groufty.dto.page.task.StudentTaskListOverviewDto;
import nl.javalon.groufty.dto.page.task.SubmissionDetailsDto;
import nl.javalon.groufty.dto.review.ReviewSummaryDto;
import nl.javalon.groufty.repository.crud.ReviewRepository;
import nl.javalon.groufty.repository.page.StudentTaskListPageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static nl.javalon.groufty.resource.RestResourceHelper.checkFound;

/**
 * Everything "My Tasks".
 * @author Lukas Miedema
 */
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PAGE_PREFIX + "student")
@Api("Everything 'My Tasks'")
public class StudentTaskListPageResource {

	@Inject	private StudentTaskListPageRepository studentTaskListPageRepository;
	@Inject private ReviewRepository reviewRepository;

	@ApiOperation("Get all task lists assigned to the current user, with a submission deadline in the future")
	@RequestMapping(value = "tasklist-overview", method = RequestMethod.GET)
	public Page<StudentTaskListOverviewDto> getTaskListOverview(Authentication authentication, Pageable page) {
		long authorId = ((User) authentication.getPrincipal()).getAuthorId();
		return studentTaskListPageRepository.getStudentTaskListOverview(authorId, page);
	}

	@ApiOperation("Get all tasks inside the provided task list")
	@RequestMapping(value = "tasklist-expand/{taskListId}", method = RequestMethod.GET)
	public Page<StudentTaskListExpandDto> getTaskListExpand(
			Authentication authentication,
			@ApiParam(required = true) @PathVariable long taskListId,
			Pageable page) {
		long authorId = ((User) authentication.getPrincipal()).getAuthorId();
		return studentTaskListPageRepository.getStudentTaskListExpand(authorId, taskListId, page);
	}

	@ApiOperation("Get details about the provided task")
	@RequestMapping(value = "task-details/{taskId}", method = RequestMethod.GET)
	public SubmissionDetailsDto getTaskDetails(
			Authentication authentication,
			@ApiParam(required = true) @PathVariable long taskId) {
		long authorId = ((User) authentication.getPrincipal()).getAuthorId();
		SubmissionDetailsDto dto = checkFound(studentTaskListPageRepository.getStudentTaskDetails(authorId, taskId));

		// Install the reviews for this task if this is after the review deadline
		if (dto.getReviewDeadline().before(new Date())) {
			List<ReviewSummaryDto> reviews = reviewRepository.findSummaryBySubmission(taskId, dto.getAuthorId());
			dto.setReviews(reviews);
		}
		return dto;
	}
}
