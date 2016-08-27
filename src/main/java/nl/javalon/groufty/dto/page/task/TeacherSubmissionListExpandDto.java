package nl.javalon.groufty.dto.page.task;

import lombok.Data;
import nl.javalon.groufty.domain.task.SubmissionContentType;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Lukas Miedema
 */
@Data
public class TeacherSubmissionListExpandDto {

	// From task
	private final long taskId;
	private final SubmissionContentType contentType;
	private final String taskName;
	private final boolean showGradesToReviewers;

	// From submission (or null, if no submission)
	private final BigDecimal grade;
	private final Date lastEdited;
	private final boolean submitted;
	private final BigDecimal lowestReviewGrade;
	private final BigDecimal highestReviewGrade;
	private final BigDecimal largestDiffReviewGrade;
	private final int submittedReviewCount;
	private final int totalReviewCount;
	private final boolean hasFlaggedReviews;

	@SqlToBeanConstructor
	public TeacherSubmissionListExpandDto(
			long taskId, SubmissionContentType contentType, String taskName, boolean showGradesToReviewers,
			BigDecimal grade, Date lastEdited, Boolean submitted, BigDecimal lowestReviewGrade,
			BigDecimal highestReviewGrade, int submittedReviewCount, int totalReviewCount, boolean hasFlaggedReviews) {
		this.taskId = taskId;
		this.contentType = contentType;
		this.taskName = taskName;
		this.showGradesToReviewers = showGradesToReviewers;
		this.grade = grade;
		this.lastEdited = lastEdited;
		this.submitted = submitted != null && submitted; // get rid of null
		this.lowestReviewGrade = lowestReviewGrade;
		this.highestReviewGrade = highestReviewGrade;
		this.submittedReviewCount = submittedReviewCount;
		this.totalReviewCount = totalReviewCount;
		this.largestDiffReviewGrade = lowestReviewGrade == null|| highestReviewGrade == null ?
				null : highestReviewGrade.subtract(lowestReviewGrade);
		this.hasFlaggedReviews = hasFlaggedReviews;
	}
}
