package nl.javalon.groufty.resource.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.dto.page.review.ReviewDetailsDto;
import nl.javalon.groufty.dto.page.review.ReviewExpandDto;
import nl.javalon.groufty.dto.page.review.ReviewOverviewDto;
import nl.javalon.groufty.repository.page.ReviewPageRepository;
import nl.javalon.groufty.util.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static nl.javalon.groufty.resource.RestResourceHelper.checkFound;

/**
 * Everything "My Reviews".
 * @author Lukas Miedema
 */
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PAGE_PREFIX)
@Api("Everything 'My Review Tasks'")
public class ReviewPageResource {

	@Inject
	private ReviewPageRepository reviewPageRepository;

	@Inject
	private FileService fileService;

	@ApiOperation("Returns overview of all tasks for which reviews have to be written")
	@RequestMapping(value = "review-overview", method = RequestMethod.GET)
	public Page<ReviewOverviewDto> getReviewOverview(Authentication authentication, Pageable page) {
		long authorId = ((User) authentication.getPrincipal()).getAuthorId();
		return reviewPageRepository.getReviewOverview(authorId, page);
	}

	@ApiOperation("Returns review details for a specific review")
	@RequestMapping(value = "review-details/{reviewId}", method = RequestMethod.GET)
	public ReviewDetailsDto getReviewDetails(Authentication authentication, @PathVariable long reviewId) {
		long authorId = ((User) authentication.getPrincipal()).getAuthorId();
		ReviewDetailsDto dto = checkFound(reviewPageRepository.getReviewDetails(authorId, reviewId));

		// Anonymize file name
		if (dto.getAnonymousSubmissions()) {
			String fileName = dto.getSubmissionFile();
			if (fileName != null) {
				dto.setSubmissionFile(fileService.anonymizeFileName(fileName));
			}
		}
		return dto;
	}

	@ApiOperation("Returns all review details for a specific task")
	@RequestMapping(value = "review-expand/{taskId}", method = RequestMethod.GET)
	public Page<ReviewExpandDto> getReviewExpand(Authentication authentication, @PathVariable long taskId, Pageable page) {
		long authorId = ((User) authentication.getPrincipal()).getAuthorId();
		return reviewPageRepository.getReviewExpand(authorId, taskId, page);
	}
}
