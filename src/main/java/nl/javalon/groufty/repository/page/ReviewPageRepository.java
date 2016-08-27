package nl.javalon.groufty.repository.page;

import nl.javalon.groufty.dto.SimplePage;
import nl.javalon.groufty.dto.page.review.ReviewDetailsDto;
import nl.javalon.groufty.dto.page.review.ReviewExpandDto;
import nl.javalon.groufty.dto.page.review.ReviewOverviewDto;
import nl.javalon.groufty.repository.AbstractNativeRepository;
import nl.javalon.groufty.util.convert.AnnotatedBeanResultTransformer;
import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Contains native query methods for the review page repository.
 * @author Lukas Miedema
 */
@Transactional
@Repository
public class ReviewPageRepository extends AbstractNativeRepository {

	private final ResultTransformer reviewDetailsTransformer;
	private final ResultTransformer reviewExpandTransformer;
	private final ResultTransformer reviewOverviewTransformer;

	@Inject
	public ReviewPageRepository(ConversionService cs) {
		this.reviewDetailsTransformer = new AnnotatedBeanResultTransformer(ReviewDetailsDto.class, cs);
		this.reviewExpandTransformer = new AnnotatedBeanResultTransformer(ReviewExpandDto.class, cs);
		this.reviewOverviewTransformer = new AnnotatedBeanResultTransformer(ReviewOverviewDto.class, cs);
	}

	/**
	 * Returns information about all tasks for which the provided author has reviews.
	 * @param authorId
	 * @param page
	 * @return
	 */
	public Page<ReviewOverviewDto> getReviewOverview(long authorId, Pageable page) {
		Query query = prepareQuery("review-overview", reviewOverviewTransformer);
		return new SimplePage<>(page, query.setParameter("author_id", authorId).list());
	}

	/**
	 * Returns information about the provided review id.
	 * @param authorId
	 * @param reviewId
	 * @return
	 */
	public ReviewDetailsDto getReviewDetails(long authorId, long reviewId) {
		Query query = prepareQuery("review-details", reviewDetailsTransformer);
		query.setParameter("author_id", authorId);
		query.setParameter("review_id", reviewId);
		return (ReviewDetailsDto) query.uniqueResult();
	}

	/**
	 * Finds all reviews for a specific task.
	 * @param authorId
	 * @param taskId
	 * @param page
	 * @return
	 */
	public Page<ReviewExpandDto> getReviewExpand(long authorId, long taskId, Pageable page) {
		Query query = prepareQuery("review-expand", reviewExpandTransformer);
		return new SimplePage<>(page, query.setParameter("author_id", authorId).setParameter("task_id", taskId).list());
	}
}
