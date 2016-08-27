package nl.javalon.groufty.dto.page.task;

import lombok.Data;
import nl.javalon.groufty.domain.task.SubmissionContentType;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.domain.user.UserType;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

import java.math.BigDecimal;

/**
 * @author Lukas Miedema
 */
@Data
public class TeacherTaskListExpandDto {

	// From task
	private final long taskId;
	private final String taskName;
	private final SubmissionContentType contentType;
	private final UserId authorUserId;
	private final String authorName;
	private final Long reviewTemplateId;
	private final String reviewTemplateName;
	private final boolean showGradesToReviewers;

	// Calculated
	private final long submittedSubmissionsCount; // nr of submissions submitted for this task
	private final long submittedReviewsCount; // nr of reviews submitted for this task
	private final long reviewsCount; // nr of reviews in the database
	private final BigDecimal averageGrade; // average grade of all submissions

	@SqlToBeanConstructor
	public TeacherTaskListExpandDto(
			long taskId, String taskName, SubmissionContentType contentType,
			UserType userType, long userNumber, String authorName,
			Long reviewTemplateId, String reviewTemplateName, boolean showGradesToReviewers,

			long submittedSubmissionsCount, long submittedReviewsCount,
			long reviewsCount, BigDecimal averageGrade) {

		this.taskId = taskId;
		this.taskName = taskName;
		this.contentType = contentType;
		this.authorUserId = new UserId(userType, userNumber);
		this.authorName = authorName;
		this.reviewTemplateId = reviewTemplateId;
		this.reviewTemplateName = reviewTemplateName;
		this.showGradesToReviewers = showGradesToReviewers;

		this.submittedSubmissionsCount = submittedSubmissionsCount;
		this.submittedReviewsCount = submittedReviewsCount;
		this.reviewsCount = reviewsCount;
		this.averageGrade = averageGrade;
	}
}
