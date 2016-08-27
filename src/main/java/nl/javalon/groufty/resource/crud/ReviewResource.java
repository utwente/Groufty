package nl.javalon.groufty.resource.crud;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.review.ReviewTemplateMismatchException;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.review.instance.ReviewFlag;
import nl.javalon.groufty.domain.review.instance.ReviewProperty;
import nl.javalon.groufty.dto.review.ReviewDto;
import nl.javalon.groufty.repository.crud.AuthorRepository;
import nl.javalon.groufty.repository.crud.ReviewRepository;
import nl.javalon.groufty.resource.NotAcceptableException;
import nl.javalon.groufty.resource.UnauthorizedException;
import nl.javalon.groufty.security.GrouftyMethodSecurityExpressionRoot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

import static nl.javalon.groufty.resource.RestResourceHelper.checkFound;

/**
 * Read and update reviews. This repository offers no way of creating reviews, as this is done by the
 * {@link nl.javalon.groufty.domain.review.ReviewerSelectionStrategy} set in the TaskList. Only users with ROLE_EDITOR
 * are allowed to list all reviews and delete reviews.
 * @author Lukas Miedema
 */
@Api(description = "Manage reviews")
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PREFIX + "reviews", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ReviewResource {

	@Inject
	private ReviewRepository reviewRepository;

	@Inject
	private AuthorRepository authorRepository;

	@ApiOperation("Retrieve all reviews")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Page<Review> getAll(Pageable pageable) {
		return reviewRepository.findAll(pageable);
	}

	@ApiOperation("Retrieve a single review")
	@PostAuthorize("hasRole('ROLE_EDITOR') || returnObject == null || (hasRole('ROLE_PARTICIPANT') && inGroup(returnObject.author))")
	@RequestMapping(value = "/{reviewId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Review get(@ApiParam(required = true) @PathVariable long reviewId) {
		return checkFound(reviewRepository.findOne(reviewId));
	}

	@ApiOperation("Update an existing review")
	@PostAuthorize("hasRole('ROLE_EDITOR') || returnObject == null || (hasRole('ROLE_PARTICIPANT') && inGroup(returnObject.author))")
	@RequestMapping(value = "/{reviewId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public Review put(
			@ApiParam(required = true) @PathVariable long reviewId,
			@ApiParam(required = true) @RequestBody @Valid ReviewDto reviewDto) throws ReviewTemplateMismatchException {

		Review review = checkFound(reviewRepository.findOne(reviewId), "No such review");

		// Check deadline
		Date now = new Date();
		if (now.after(review.getSubmission().getId().getTask().getTaskList().getReviewDeadline())) {
			throw new NotAcceptableException("After deadline");
		}

		// Update
		review.setSubmitted(reviewDto.getSubmitted());

		List<ReviewProperty> reviewProperties = review.getReviewProperties();
		reviewProperties.clear();
		reviewProperties.addAll(reviewDto.getReviewProperties());
		review.getReviewProperties().forEach(p -> p.setReview(review));

		review.recalculateGrade();
		return review;
	}

	@ApiOperation("Flag a review")
	@RequestMapping(value = "/{reviewId}/flag", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void putFlag(
			@ApiParam(required = true) @PathVariable long reviewId,
			@ApiParam(required = true) @RequestBody @Valid ReviewFlag reviewFlag,
			GrouftyMethodSecurityExpressionRoot expressionRoot) {
		Review review = checkFound(reviewRepository.findOne(reviewId));

		// Security check
		if (!expressionRoot.hasRole("ROLE_EDITOR") && !expressionRoot.inGroup(review.getSubmission().getAuthorId()))
			throw new UnauthorizedException();

		if (review.getFlag() != null) {
			throw new NotAcceptableException("This review has already been flagged. It cannot be flagged twice");
		}
		reviewFlag.setReview(review);
		review.setFlag(reviewFlag);
	}

	@ApiOperation("Delete a review")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(value = "/{reviewId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void delete(@ApiParam(required = true) @PathVariable long reviewId) {
		reviewRepository.delete(reviewId);
	}
}
