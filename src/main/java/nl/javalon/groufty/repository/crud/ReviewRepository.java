package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.review.template.ReviewTemplate;
import nl.javalon.groufty.dto.review.ReviewSummaryDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ReviewRepository extends PagingAndSortingRepository<Review, Long> {

	/**
	 * Finds all revies and creates a summary for the provided submission identifiers. Anonymous reviews will not contain
	 * the name and not-submitted or disabled reviews will not be included.
	 * @param submissionTaskId
	 * @param submissionAuthorId
	 * @return
	 */
	@Query("SELECT NEW nl.javalon.groufty.dto.review.ReviewSummaryDto(r, r.submission.id.submissionList.id.taskList.anonymousReviews)\n" +
			"FROM Review r\n" +
			"WHERE r.submission.id.task.taskId = ?1\n" +
			"AND r.submission.id.submissionList.id.author.authorId = ?2\n" +
			"AND r.submitted = true\n" +
			"AND r.disabled = false\n" +
			"ORDER BY r.reviewId ASC")
	List<ReviewSummaryDto> findSummaryBySubmission(long submissionTaskId, long submissionAuthorId);

	/**
	 * Finds all revies and creates a summary for the provided submission identifiers, without anonymization, including
	 * disabled and not-submitted reviews.
	 * @param submissionTaskId
	 * @param submissionAuthorId
	 * @return
	 */
	@Query("SELECT NEW nl.javalon.groufty.dto.review.ReviewSummaryDto(r)\n" +
			"FROM Review r\n" +
			"WHERE r.submission.id.task.taskId = ?1\n" +
			"AND r.submission.id.submissionList.id.author.authorId = ?2\n" +
			"ORDER BY r.reviewId ASC")
	List<ReviewSummaryDto> findSummaryBySubmissionAll(long submissionTaskId, long submissionAuthorId);

	/**
	 * Finds all reviews with the specified review template.
	 * @param template
	 * @return
	 */
	@Query("FROM Review r WHERE r.submission.id.task.reviewTemplate = ?1")
	List<Review> findByReviewTemplate(ReviewTemplate template);
}
