package nl.javalon.groufty.domain.task;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.review.ReviewerSelectionStrategy;
import nl.javalon.groufty.domain.user.Grouping;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserId;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * Represents a collection of {@link Task} objects.
 */
@Entity
@Table(name = "task_list",
		indexes = @Index(name = "task_list_name_index", columnList = "name", unique = true)
)
@Getter
@Setter
@EqualsAndHashCode(of = "taskListId")
public class TaskList {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "task_list_id", nullable = false)
	@NotNull
	private long taskListId;

	@Column(name = "name", nullable = false, unique = true)
	@NotNull
	private String name;

	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	private TaskListState state;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "submission_deadline")
	private Date submissionDeadline;

	@Column(name = "review_deadline")
	private Date reviewDeadline;

	@Column(name = "anonymous_reviews", nullable = false)
	@ColumnDefault("false")
	private boolean anonymousReviews = false;

	@Column(name = "anonymous_submissions", nullable = false)
	@ColumnDefault("false")
	private boolean anonymousSubmissions = false;

	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "author_id")
	private User author;

	@Enumerated(EnumType.STRING)
	@Column(name = "submission_audience", nullable = false)
	@NotNull
	private Audience submissionAudience;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "grouping_id")
	private Grouping grouping;

	@JsonIgnore
	@OneToMany(mappedBy = "id.taskList", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<SubmissionList> submissionsLists;

	@JsonIgnore
	@OneToMany(mappedBy = "taskList", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Task> tasks;

	@OneToOne(mappedBy = "taskList", cascade = CascadeType.ALL, orphanRemoval = true)
	private ReviewerSelectionStrategy reviewerSelectionStrategy;

	// Derived properties
	@JsonGetter
	public UserId getAuthorUserId() {
		return author.getUserId();
	}

	@JsonGetter
	public Long getGroupingId() {
		return grouping == null ? null : grouping.getGroupingId();
	}

	// Validation
	@AssertTrue(message = "Dates can only be null when the submission is in the Draft state.")
	private boolean checkDatesValid() {
		return state == TaskListState.DRAFT ||
				(startDate != null && submissionDeadline != null && reviewDeadline != null);
	}

	@AssertTrue(message = "Grouping must be assigned when submission is not in the Draft state")
	private boolean checkGroupingValid() {
		return state == TaskListState.DRAFT || grouping != null;
	}

	@AssertTrue(message = "Reviewer selection strategy must be assigned when submission is not in the Draft state")
	private boolean checkReviewerSelectionStrategyValid() {
		return state == TaskListState.DRAFT || reviewerSelectionStrategy != null;
	}
}
