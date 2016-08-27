package nl.javalon.groufty.domain.review;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.task.Audience;
import nl.javalon.groufty.domain.task.Submission;
import nl.javalon.groufty.domain.task.TaskList;
import nl.javalon.groufty.domain.user.Author;
import nl.javalon.groufty.domain.user.Grouping;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * A {@link ReviewerSelectionStrategy} specifies how reviewers should be assigned
 * by a Task after the submissionDeadline expires.
 */
@Entity
@Table(name = "reviewer_selection_strategy")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonSubTypes({
		@JsonSubTypes.Type(value = RandomReviewerSelectionStrategy.class, name = "RANDOM_REVIEWER_SELECTION_STRATEGY"),
		@JsonSubTypes.Type(value = KnownReviewerSelectionStrategy.class, name = "KNOWN_REVIEWER_SELECTION_STRATEGY")
})
@Getter
@Setter
public abstract class ReviewerSelectionStrategy {

	@Id
	private long taskListId;

	@MapsId
	@OneToOne(optional = false)
	@JoinColumn(name = "task_list_id", nullable = false)
	private TaskList taskList;

	@Column(name = "review_count", nullable = false)
	private int reviewCount; // number of reviews to expect for every assignment

	@ManyToOne
	@JoinColumn(name = "from_grouping_id", nullable = false)
	private Grouping from;

	@Column(name = "review_audience", nullable = false)
	@Enumerated(EnumType.STRING)
	private Audience reviewAudience;

	@Column(name = "primed", nullable = false)
	private boolean primed; // when true, execute this strategy when the deadline expires

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "execution_date")
	private Date executionDate; // null when the reviewer selection hasn't happened yet

	@Column(name = "error_message")
	private String errorMessage; // when failed, the last message will be here

	/**
	 * Applies the provided strategy.
	 * @throws IllegalStateException when not all preconditions are met
	 */
	public abstract Map<Submission, Set<Author>> performSelection() throws ReviewSelectionStrategyPerformException;
}
