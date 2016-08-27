package nl.javalon.groufty.domain.review.instance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.review.ReviewTemplateMismatchException;
import nl.javalon.groufty.domain.review.template.ReviewTemplate;
import nl.javalon.groufty.domain.task.Submission;
import nl.javalon.groufty.domain.user.Author;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Represents a review instance. Has a list of {@link ReviewProperty} objects detailing the review.
 */
@Entity
@Table(name = "review")
@Getter
@Setter
public class Review {

	/**
	 * Placeholder name to use when reviews are anonymous.
	 */
	public static final String ANONYMOUS_AUTHOR_NAME = "Anonymous";

	@Id
	@GeneratedValue
	@Column(name = "review_id", nullable = false)
	private long reviewId;

	@Column(name = "last_edited", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date lastEdited;

	@Column(name = "submitted", nullable = false)
	private boolean submitted = false;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	@JsonIgnore
	private Author author;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "submission_task_id", referencedColumnName = "task_id", nullable = false),
			@JoinColumn(name = "submission_author_id", referencedColumnName = "author_id", nullable = false),
			@JoinColumn(name = "submission_task_list_id", referencedColumnName = "task_list_id", nullable = false)
	})
	@JsonIgnore
	private Submission submission;

	@Column(name = "grade")
	private BigDecimal grade;

	@OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy
	private List<ReviewProperty> reviewProperties;

	@Column(name = "disabled", nullable = false)
	@ColumnDefault("false")
	private boolean disabled;

	@OneToOne(mappedBy = "review", cascade = CascadeType.PERSIST)
	private ReviewFlag flag;

	/**
	 * Calculates and sets the {@link #grade} field based on the filled in ReviewTemplate and Review.
	 */
	public void recalculateGrade() throws ReviewTemplateMismatchException {

		// Get the review template
		ReviewTemplate reviewTemplate = submission.getId().getTask().getReviewTemplate();

		if (reviewTemplate == null) {
			// No sensible grade can be calculated
			if (this.reviewProperties != null && !this.reviewProperties.isEmpty()) {
				throw new ReviewTemplateMismatchException("No review template set but review properties are present.");
			}
			this.grade = null;
			return;
		}

		if (this.reviewProperties == null || this.reviewProperties.isEmpty()) {
			// Not filled in yet
			this.grade = null;
			return;
		}

		// Calculate
		this.grade = reviewTemplate.calculateGrade(reviewProperties);

		// With the change of this grade, update the grade of the submission as well
		this.submission.recalculateGrade();
	}
}
