package nl.javalon.groufty.dto.page.task;

import lombok.Data;
import nl.javalon.groufty.domain.task.Audience;
import nl.javalon.groufty.domain.task.TaskListState;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.domain.user.UserType;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

import java.util.Date;

/**
 * @author Lukas Miedema
 */
@Data
public class TeacherTaskListOverviewDto {
	
	// Task list properties
	private final long taskListId;
	private final String taskListName;
	private final TaskListState state;
	private final Date startDate;
	private final Date submissionDeadline;
	private final Date reviewDeadline;
	private final boolean anonymousReviews;
	private final boolean anonymousSubmissions;
	private final UserId authorUserId; // user_type, user_number
	private final String authorName;
	private final Audience submissionAudience;

	// Extra properties
	private final long groupingId;
	private final String groupingName;
	private final long taskCount;
	private final long submittedSubmissionsCount;
	private final long submittedReviewsCount;
	private final long submitterCount; // nr of authors that can submit submissions * task_count, to get the max number of submissions
	private final long reviewerCount; // nr of authors that can submit reviews (number of reviews in the db for this tl)

	@SqlToBeanConstructor
	public TeacherTaskListOverviewDto(
			long taskListId, String taskListName, TaskListState state, Date startDate, Date submissionDeadline,
			Date reviewDeadline, boolean anonymousReviews, boolean anonymousSubmissions, long userId,
			UserType userType, String authorName, Audience submissionAudience, long groupingId, String groupingName,
			long taskCount,	long submittedSubmissionsCount, long submittedReviewsCount,	long submitterCount,
			long reviewerCount) {

		this.taskListId = taskListId;
		this.taskListName = taskListName;
		this.state = state;
		this.startDate = startDate;
		this.submissionDeadline = submissionDeadline;
		this.reviewDeadline = reviewDeadline;
		this.anonymousReviews = anonymousReviews;
		this.anonymousSubmissions = anonymousSubmissions;
		this.authorUserId = new UserId(userType, userId);
		this.authorName = authorName;
		this.submissionAudience = submissionAudience;
		this.groupingId = groupingId;
		this.groupingName = groupingName;
		this.taskCount = taskCount;
		this.submittedSubmissionsCount = submittedSubmissionsCount;
		this.submittedReviewsCount = submittedReviewsCount;
		this.submitterCount = submitterCount;
		this.reviewerCount = reviewerCount;
	}
}
