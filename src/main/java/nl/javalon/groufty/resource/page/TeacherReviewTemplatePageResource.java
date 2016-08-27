package nl.javalon.groufty.resource.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.dto.page.review.TeacherReviewTemplateInfoDto;
import nl.javalon.groufty.repository.page.TeacherReviewTemplatePageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Everything "Review Templates".
 * @author Lukas Miedema
 */
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PAGE_PREFIX + "teacher")
@Api("Everything 'Review Templates'")
public class TeacherReviewTemplatePageResource {

	@Inject	private TeacherReviewTemplatePageRepository reviewTemplatePageRepository;

	@ApiOperation("Get all review templates in the system with relevant information")
	@RequestMapping(value = "reviewtemplate-overview", method = RequestMethod.GET)
	public Page<TeacherReviewTemplateInfoDto> getReviewTemplateOverview(Pageable page) {
		return reviewTemplatePageRepository.getTeacherReviewTemplateOverview(page);
	}

	@ApiOperation("Get information about a specific review template")
	@RequestMapping(value = "reviewtemplate-details/{reviewTemplateId}", method = RequestMethod.GET)
	public TeacherReviewTemplateInfoDto getReviewTemplateDetails(@PathVariable long reviewTemplateId) {
		return reviewTemplatePageRepository.getTeacherReviewTemplateDetails(reviewTemplateId);
	}
}