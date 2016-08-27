package nl.javalon.groufty.dto.page.task;

import lombok.Data;
import nl.javalon.groufty.domain.task.SubmissionContentType;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Data transfer object for student task list expand data.
 * Encapsulates a Submissions for a student with some aggregated data from the reviews.
 * See student-tasklist-expand.sql for how this data is computed.
 * @author Lukas Miedema
 */
@Data
public class StudentTaskListExpandDto {

	private final long taskId;
	private final String taskFileName;
	private final String taskName;
	private final Date lastEdited;
	private final boolean submitted;
	private final SubmissionContentType contentType;
	private final String submissionFileName;
	private final BigDecimal grade;

	/**
	 * Constructor to be invoked by {@link nl.javalon.groufty.util.convert.AnnotatedBeanResultTransformer}
	 * @param taskId
	 * @param taskFileName
	 * @param name
	 * @param lastEdited
	 * @param submitted
	 * @param submissionFileName
	 * @param grade
	 * @param reviewDeadline
	 */
	@SqlToBeanConstructor
	public StudentTaskListExpandDto(long taskId, String taskFileName, String name, SubmissionContentType contentType,
	                                Date lastEdited, Boolean submitted, String submissionFileName, BigDecimal grade,
									Date reviewDeadline) {
		this.taskId = taskId;
		this.taskFileName = taskFileName;
		this.taskName = name;
		this.contentType = contentType;
		this.lastEdited = lastEdited;
		this.submitted = submitted == null ? false : submitted;
		this.submissionFileName = submissionFileName;

		// Determine if we should show grade
		Date now = new Date();
		if (now.after(reviewDeadline)) {
			this.grade = grade;
		} else {
			this.grade = null;
		}
	}
}
