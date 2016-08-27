package nl.javalon.groufty.csv.sheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.javalon.groufty.domain.task.Audience;
import nl.javalon.groufty.domain.task.TaskList;
import nl.javalon.groufty.domain.task.TaskListState;
import nl.javalon.groufty.domain.user.UserId;

import java.util.Date;

/**
 * @author Lukas Miedema
 */
@JsonPropertyOrder({"name", "state", "startDate", "submissionDeadline", "reviewDeadline", "anonymousReviews",
		"anonymousSubmissions", "authorUserId", "submissionAudience", "groupingName", "reviewSelectionStrategy"})
@Data
@NoArgsConstructor
public class TaskListSheet {

	public TaskListSheet(TaskList taskList) {
		setName(taskList.getName());
		setState(taskList.getState());
		setStartDate(taskList.getStartDate());
		setSubmissionDeadline(taskList.getSubmissionDeadline());
		setReviewDeadline(taskList.getReviewDeadline());
		setAnonymousReviews(taskList.isAnonymousReviews());
		setAnonymousSubmissions(taskList.isAnonymousSubmissions());
		setAuthorUserId(taskList.getAuthorUserId());
		setSubmissionAudience(taskList.getSubmissionAudience());
		setGroupingName(taskList.getGrouping().getGroupingName());
	}

	@JsonProperty(value = "Task list name", required = true)
	private String name;

	@JsonProperty(value = "State", required = false)
	private TaskListState state;

	@JsonProperty(value = "Start date", required = true)
	private Date startDate;

	@JsonProperty(value = "Submission deadline", required = true)
	private Date submissionDeadline;

	@JsonProperty(value = "Review deadline", required = true)
	private Date reviewDeadline;

	@JsonProperty(value = "Anonymous reviews", required = true)
	private boolean anonymousReviews;

	@JsonProperty(value = "Anonymous submissions", required = true)
	private boolean anonymousSubmissions;

	@JsonProperty(value = "Task list author", required = true)
	private UserId authorUserId;

	@JsonProperty(value = "Submission audience", required = true)
	private Audience submissionAudience;

	@JsonProperty(value = "Grouping", required = true)
	private String groupingName;

	@JsonUnwrapped
	private ReviewSelectionStrategySheet reviewSelectionStrategy;

	@JsonPropertyOrder({"reviewSelectionStrategy", "reviewsPerTask", "fromGrouping", "reviewAudience", "skipSubmissionlessAuthors"})
	@Data
	public static class ReviewSelectionStrategySheet {

		@JsonProperty(value = "Review selection strategy")
		private String reviewSelectionStrategy;

		// Common types
		@JsonProperty(value = "Reviews per task")
		private int reviewsPerTask;

		@JsonProperty(value = "From grouping")
		private String fromGrouping;

		@JsonProperty(value = "Review audience")
		private Audience reviewAudience;

		@JsonProperty(value = "Skip submissionless authors")
		private boolean skipSubmissionlessAuthors;
	}
}
