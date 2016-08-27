package nl.javalon.groufty.dto.review;

import lombok.Data;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.user.Author;
import nl.javalon.groufty.domain.user.Group;
import nl.javalon.groufty.domain.user.User;

import java.math.BigDecimal;

/**
 * Contains review id, review author name (or anonymous) and grade.
 * The grade will be hidden (null) until the submission list is not yet finalized.
 * @author Lukas Miedema
 */
@Data
public class ReviewSummaryDto {

	private final long reviewId;
	private final String authorName;
	private final BigDecimal grade;
	private final boolean flagged;
	private final boolean submitted;
	private final boolean disabled;

	/**
	 * Construct a new {@link ReviewSummaryDto} ignoring anonymity
	 * @param review
	 */
	public ReviewSummaryDto(Review review) {
		this(review, false);
	}

	/**
	 * Constructs a new {@link ReviewSummaryDto}
	 * To be invoked via HQL
	 * @param review the review to summarize
	 * @param anonymousReview should reviews be anonymous?
	 */
	public ReviewSummaryDto(Review review, boolean anonymousReview) {
		this.reviewId = review.getReviewId();
		this.grade = review.getSubmission().getId().getSubmissionList().isFinalized() ? review.getGrade() : null;

		this.flagged = review.getFlag() != null;
		this.submitted = review.isSubmitted();
		this.disabled = review.isDisabled();

		if (anonymousReview) {
			this.authorName = Review.ANONYMOUS_AUTHOR_NAME;
		} else {
			Author author = review.getAuthor();
			this.authorName = author instanceof User ? ((User) author).getFullName() : ((Group) author).getGroupName();
		}
	}
}
