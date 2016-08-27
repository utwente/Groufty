package nl.javalon.groufty.resource.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.dto.page.task.SubmissionDetailsDto;
import nl.javalon.groufty.dto.page.task.TeacherSubmissionListExpandDto;
import nl.javalon.groufty.dto.page.task.TeacherSubmissionListOverviewDto;
import nl.javalon.groufty.dto.review.ReviewSummaryDto;
import nl.javalon.groufty.repository.crud.ReviewRepository;
import nl.javalon.groufty.repository.page.TeacherSubmissionListPageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static nl.javalon.groufty.resource.RestResourceHelper.checkFound;

/**
 * Everything "Submission lists".
 * @author Lukas Miedema
 */
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PAGE_PREFIX + "teacher")
@Api("Everything 'Submission lists'")
public class TeacherSubmissionListPageResource {

	@Inject	private TeacherSubmissionListPageRepository teacherSubmissionListPageRepository;
	@Inject private ReviewRepository reviewRepository;

	@ApiOperation("Get all submission lists for a certain task list with relevant information")
	@RequestMapping(value = "submissionlist-overview/{taskListId}", method = RequestMethod.GET)
	public Page<TeacherSubmissionListOverviewDto> getSubmissionListOverview(
			@PathVariable long taskListId,
			@RequestParam(required = false) BigDecimal difference,
			Pageable page) {
		return teacherSubmissionListPageRepository.getTeacherTaskListOverview(
				taskListId, difference == null ? BigDecimal.ZERO: difference, difference != null, page);
	}

	@ApiOperation("Get all submissions for a specific submission list with relevant information")
	@RequestMapping(value = "submissionlist-expand/{taskListId}/{authorId}", method = RequestMethod.GET)
	public Page<TeacherSubmissionListExpandDto> getSubmissionListExpand(
			@PathVariable long taskListId, @PathVariable long authorId, Pageable page) {
		return teacherSubmissionListPageRepository.getTeacherTaskListExpand(taskListId, authorId, page);
	}

	@ApiOperation("Get details about the provided task")
	@RequestMapping(value = "submission-details/{taskId}/{authorId}", method = RequestMethod.GET)
	public SubmissionDetailsDto getTaskDetails(
			Authentication authentication,
			@ApiParam(required = true) @PathVariable long taskId,
			@ApiParam(required = true) @PathVariable long authorId) {
		SubmissionDetailsDto dto = checkFound(teacherSubmissionListPageRepository.getTeacherSubmissionDetails(authorId, taskId));

		// Install the reviews for this task if this is after the review deadline
		if (dto.getReviewDeadline().before(new Date())) {
			List<ReviewSummaryDto> reviews = reviewRepository.findSummaryBySubmissionAll(taskId, dto.getAuthorId());
			dto.setReviews(reviews);
		}
		return dto;
	}
}