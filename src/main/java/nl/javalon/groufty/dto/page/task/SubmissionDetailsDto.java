package nl.javalon.groufty.dto.page.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.javalon.groufty.domain.task.Audience;
import nl.javalon.groufty.domain.task.SubmissionContentType;
import nl.javalon.groufty.domain.task.TaskListState;
import nl.javalon.groufty.dto.review.ReviewSummaryDto;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Used for both students and teachers
 * View a single submission
 * @author Lukas Miedema
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubmissionDetailsDto extends StudentTaskListExpandDto {

	private final String description; // in markdown, from task

	private final String submissionText;

	// Data from task list
	private final Long reviewTemplateId;
	private final Date startDate;
	private final Date submissionDeadline;
	private final Date reviewDeadline;
	private final String taskListName;
	private final TaskListState taskListState;

	// Author data. This is from the group or user that submitted it
	private final long authorId;
	private final String authorName; // either the group name or the users full name
	private final Audience submissionAudience; // either INDIVIDUAL or GROUP

	private List<ReviewSummaryDto> reviews = new LinkedList<>(); // list of review summaries. Has to be populated externally

	/**
	 * Constructor to be invoked by an {@link nl.javalon.groufty.util.convert.AnnotatedBeanResultTransformer}
	 *
	 * @param taskId
	 * @param taskFileName
	 * @param name
	 * @param lastEdited
	 * @param submitted
	 * @param submissionText
	 * @param submissionFileName
	 * @param grade
	 */
	@SqlToBeanConstructor
	public SubmissionDetailsDto(long taskId, String taskFileName, String name, String description, SubmissionContentType contentType,
	                            Long reviewTemplateId, Date lastEdited, Boolean submitted, String submissionText,
	                            String submissionFileName, BigDecimal grade, String taskListName, Date reviewDeadline,
	                            Date startDate, TaskListState taskListState, Date submissionDeadline, Audience submissionAudience,
	                            long authorId, String authorName) throws SQLException {
		super(taskId, taskFileName, name, contentType, lastEdited, submitted, submissionFileName, grade, reviewDeadline);

		// From task
		this.description = description;
		this.reviewTemplateId = reviewTemplateId;

		// Task list data
		this.startDate = startDate;
		this.submissionDeadline = submissionDeadline;
		this.reviewDeadline = reviewDeadline;
		this.taskListName = taskListName;
		this.taskListState = taskListState;

		// Author data
		this.authorId = authorId;
		this.authorName = authorName;
		this.submissionAudience = submissionAudience;

		// Text
		this.submissionText = submissionText;
	}
}
