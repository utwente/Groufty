package nl.javalon.groufty.dto.page.review;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.javalon.groufty.domain.task.SubmissionContentType;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Lukas Miedema
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReviewDetailsDto extends ReviewExpandDto {

	private final Date reviewDeadline;

	private String submissionFile;
	private final String submissionText;

	private final BigInteger taskId;
	private final String taskName;
	private final String taskFileName;
	private final String taskDescription;

	private final BigInteger reviewTemplateId;
	private final SubmissionContentType submissionContentType;
	private final Boolean showGradesToReviewers;

	@SqlToBeanConstructor
	public ReviewDetailsDto(
			// For super
			BigInteger reviewId, String reviewerName, String reviewType, String submitterName, String submissionType,
	        Boolean submitted, Date lastEdited, BigDecimal grade,
			Boolean anonymousSubmissions, Boolean anonymousReviews,

			// Instance
			Date reviewDeadline, String submissionFile,	String submissionText, String submissionContentType,
			BigInteger taskId, String taskName, String taskFileName, String taskDescription,
			BigInteger reviewTemplateId, Boolean showGradesToReviewers) {

		super(reviewId, reviewerName, reviewType, submitterName, submissionType, submitted, lastEdited, grade,
				anonymousSubmissions, anonymousReviews);

		this.reviewDeadline = reviewDeadline;
		this.submissionFile = submissionFile;
		this.submissionText = submissionText;
		this.submissionContentType = SubmissionContentType.valueOf(submissionContentType);
		this.taskId = taskId;
		this.taskName = taskName;
		this.taskFileName = taskFileName;
		this.taskDescription = taskDescription;
		this.reviewTemplateId = reviewTemplateId;
		this.showGradesToReviewers = showGradesToReviewers;
	}
}
