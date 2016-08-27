package nl.javalon.groufty.dto.page.review;

import lombok.Getter;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.task.Audience;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Lukas Miedema
 */
@Getter
public class ReviewExpandDto {

	private final BigInteger reviewId;
	private final String reviewerName;
	private final Audience reviewAudience;
	private final String submitterName;
	private final Audience submissionAudience;
	private final Boolean submitted;
	private final Date lastEdited;
	private final BigDecimal grade;
	private final Boolean anonymousSubmissions;
	private final Boolean anonymousReviews;

	@SqlToBeanConstructor
	public ReviewExpandDto(BigInteger reviewId, String reviewerName, String reviewAudience, String submitterName,
						   String submissionAudience, Boolean submitted, Date lastEdited, BigDecimal grade,
						   Boolean anonymousSubmissions, Boolean anonymousReviews) {
		this.reviewId = reviewId;
		this.reviewerName = reviewerName;
		this.reviewAudience = Audience.valueOf(reviewAudience);
		this.submitterName = anonymousSubmissions ? Review.ANONYMOUS_AUTHOR_NAME : submitterName;
		this.submissionAudience = Audience.valueOf(submissionAudience);
		this.submitted = submitted;
		this.lastEdited = lastEdited;
		this.grade = grade;
		this.anonymousSubmissions = anonymousSubmissions;
		this.anonymousReviews = anonymousReviews;
	}
}
