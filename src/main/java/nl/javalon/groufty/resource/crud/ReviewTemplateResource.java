package nl.javalon.groufty.resource.crud;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.review.ReviewTemplateMismatchException;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.review.template.ReviewTemplate;
import nl.javalon.groufty.repository.crud.ReviewRepository;
import nl.javalon.groufty.repository.crud.ReviewTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Resource for {@link ReviewTemplate}'s. ReviewTemplates are not validated server-side, except if they adhere to the
 * proper class structure (and thus can be deserialized to Java POJOs). Everyone can read ReviewTemplates.
 * @author Lukas Miedema
 */
@Api(description = "Manage review templates")
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PREFIX + "reviewtemplates", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ReviewTemplateResource {

	@Inject
	private ReviewTemplateRepository reviewTemplateRepository;

	@Inject
	private ReviewRepository reviewRepository;

	@ApiOperation("Retrieve all review templates")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Page<ReviewTemplate> getAll(Pageable pageable) {
		return reviewTemplateRepository.findAll(pageable);
	}

	@ApiOperation("Retrieve a single review template")
	@RequestMapping(value = "/{reviewTemplateId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public ReviewTemplate get(@ApiParam(required = true) @PathVariable long reviewTemplateId) {
		return reviewTemplateRepository.findOne(reviewTemplateId);
	}

	@ApiOperation("Delete a review template")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(value = "/{reviewTemplateId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void delete(@ApiParam(required = true) @PathVariable long reviewTemplateId) {
		reviewTemplateRepository.delete(reviewTemplateId);
	}

	@ApiOperation("Create a review template")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public ReviewTemplate post(@ApiParam(required = true) @RequestBody ReviewTemplate reviewTemplate) {
		reviewTemplate.setReviewTemplateId(0);
		return reviewTemplateRepository.save(reviewTemplate);
	}

	@ApiOperation("Update a review template - this will recalculate any dependant review and submission grades")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(value = "/{reviewTemplateId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public ReviewTemplate put(
			@ApiParam(required = true) @PathVariable long reviewTemplateId,
			@ApiParam(required = true) @RequestBody ReviewTemplate reviewTemplate) throws ReviewTemplateMismatchException {

		// copy id. This will make the entity "not new", causing #save to merge instead of persist
		reviewTemplate.setReviewTemplateId(reviewTemplateId);
		reviewTemplate = reviewTemplateRepository.save(reviewTemplate);

		// this template already existed, so there might be reviews
		// recalculate all review grades
		List<Review> dependantReviews = reviewRepository.findByReviewTemplate(reviewTemplate);
		for (Review dependantReview : dependantReviews) {

			// If this throws an exception, just let it propagate up
			// That probably means this review template changed more than the signature
			dependantReview.recalculateGrade();
		}
		return reviewTemplate;
	}
}
