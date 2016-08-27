package nl.javalon.groufty.dto.page.task;

import lombok.Data;
import nl.javalon.groufty.domain.task.Audience;
import nl.javalon.groufty.domain.task.TaskListState;
import nl.javalon.groufty.dto.page.SubmissionListState;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Data transfer object for student task overview data.
 * Encapsulates a Submission List for a student with some aggregated data from the submissions.
 * See student-tasklist-overview.sql for how this data is computed.
 * @author Lukas Miedema
 */
@Data
public class StudentTaskListOverviewDto {

	// Primary key for future lookups
	private final BigInteger authorId;
	private final BigInteger taskListId;

	// Author data
	private final String authorName;

	// Dates
	private final Date lastEdited;
	private final Date submissionDeadline;
	private final Date reviewDeadline;

	// Data from task list
	private final String taskListName;
	private final Audience submissionAudience;

	// Data from submissions and tasks
	private final BigInteger taskCount; // the nr of tasks in the task list
	private final BigInteger submittedSubmissionsCount; // the nr of "submitted" submissions in the db for this

	// Submission list aggregate data
	private final SubmissionListState state;

	// Current grade
	private final BigDecimal grade;

	/**
	 * Constructor to be invoked by a {@link org.hibernate.transform.AliasToBeanConstructorResultTransformer}
	 * @param taskListId
	 * @param name
	 * @param reviewDeadline
	 * @param startDate
	 * @param taskListState
	 * @param submissionDeadline
	 * @param audience
	 * @param authorId
	 * @param authorName
	 * @param finalGrade
	 * @param lastEdited
	 * @param taskCount
	 * @param submittedSubmissionsCount
	 */
	@SqlToBeanConstructor
	public StudentTaskListOverviewDto(BigInteger taskListId, String name, Date reviewDeadline, Date startDate,
	                                  String taskListState, Date submissionDeadline, String audience, BigInteger authorId,
	                                  String authorName, BigDecimal finalGrade, Date lastEdited,
	                                  BigInteger taskCount, BigInteger submittedSubmissionsCount) {
		// PK
		this.authorId = authorId;
		this.taskListId = taskListId;

		this.taskListName = name;

		// Dates
		this.lastEdited = lastEdited;
		this.reviewDeadline = reviewDeadline;
		this.submissionDeadline = submissionDeadline;

		// Aggregate data
		this.taskCount = taskCount;
		this.submittedSubmissionsCount = submittedSubmissionsCount;

		// Author data
		this.authorName = authorName;
		this.submissionAudience = Audience.valueOf(audience);

		// Determine state
		// Are we before the submissionDeadline?
		Date now = new Date();
		SubmissionListState state = null;
		if (now.before(submissionDeadline)) {
			if (taskCount == this.submittedSubmissionsCount)
				state = SubmissionListState.SUBMITTED;
			else
				state = lastEdited == null ? SubmissionListState.OPEN : SubmissionListState.IN_PROGRESS;
		} else {
			// After submissionDeadline
			if (now.before(reviewDeadline)) {
				state = SubmissionListState.UNDER_REVIEW;
			} else {
				state = taskListState.equals(TaskListState.FINALIZED.name())
						? SubmissionListState.FINALIZED : SubmissionListState.REVIEWED;
			}
		}
		this.state = state;
		this.grade = finalGrade;
	}
}
