package nl.javalon.groufty.dto.page.task;

import lombok.Data;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.domain.user.UserType;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

import java.math.BigDecimal;

/**
 * @author Lukas Miedema
 */
@Data
public class TeacherSubmissionListOverviewDto {

	private final long authorId;
	private final UserId userId;
	private final String authorName;
	private final boolean finalized;
	private final BigDecimal overrideFinalGrade;
	private final long submissionCount;
	private final BigDecimal calculatedGrade;
	private final long submittedReviewCount;
	private final long totalReviewCount;
	private final BigDecimal largestDiffReviewGrade;
	private final boolean hasFlaggedReviews;

	@SqlToBeanConstructor
	public TeacherSubmissionListOverviewDto(
			long authorId, UserType userType, Long userNumber, String authorName,
			Boolean finalized, BigDecimal overrideFinalGrade, long submissionCount, BigDecimal calculatedGrade,
			long submittedReviewCount, long totalReviewCount, BigDecimal largestDiffReviewGrade, boolean hasFlaggedReviews) {

		this.authorId = authorId;
		this.userId = userType == null ? null : new UserId(userType, userNumber);
		this.authorName = authorName;
		this.finalized = finalized != null && finalized;
		this.overrideFinalGrade = overrideFinalGrade;
		this.submissionCount = submissionCount;
		this.calculatedGrade = calculatedGrade;
		this.submittedReviewCount = submittedReviewCount;
		this.totalReviewCount = totalReviewCount;
		this.largestDiffReviewGrade = largestDiffReviewGrade;
		this.hasFlaggedReviews = hasFlaggedReviews;
	}
}
