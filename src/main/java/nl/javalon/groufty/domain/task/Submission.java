package nl.javalon.groufty.domain.task;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.review.UnscaledGrade;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.util.FileDetails;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Lukas Miedema
 */
@Entity
@Table(name = "submission")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Submission {

	@JsonIgnore
	@EmbeddedId
	private SubmissionPk id;

	@Column(name = "last_edited", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date lastEdited;

	@ApiModelProperty("Null if no text has been uploaded or this is a FILE_SUBMISSION")
	@Column(name = "text", columnDefinition = "TEXT")
	private String text;

	@Column(name = "submitted", nullable = false)
	private boolean submitted = false;

	@Embedded
	private FileDetails fileDetails; // optional, if a file is included

	@Column(name = "grade")
	private BigDecimal grade;

	@JsonIgnore
	@OneToMany(mappedBy = "submission", fetch = FetchType.LAZY)
	private Set<Review> reviews;

	// Derived properties for JSON
	@JsonGetter
	public long getTaskId() {
		return id.getTask().getTaskId();
	}

	@JsonGetter
	public long getTaskListId() {
		return id.getSubmissionList().getTaskListId();
	}

	@JsonGetter
	public long getAuthorId() {
		return id.getSubmissionList().getAuthorId();
	}

	/**
	 * Recalculates the grade based on reviews. Grade will be set to null if there are no reviews
	 */
	public void recalculateGrade() {
		// Retrieve all submitted reviews
		Set<Review> submittedReviews = reviews.stream()
				.filter(Review::isSubmitted)
				.filter(r -> !r.isDisabled())
				.collect(Collectors.toSet());

		// Check count
		// Compute average
		Optional<BigDecimal> sum = submittedReviews.stream().map(Review::getGrade).reduce(BigDecimal::add);
		if (sum.isPresent()) {
			this.grade = sum.get().divide(new BigDecimal(submittedReviews.size()), UnscaledGrade.MATH_CONTEXT);
		} else {
			this.grade = null;
		}
	}

	/**
	 * Primary key for a submission
	 */
	@Data
	@Embeddable
	public static class SubmissionPk implements Serializable {

		@ManyToOne
		@JoinColumn(name = "task_id", referencedColumnName = "task_id", nullable = false)
		private Task task;

		@ManyToOne
		@JoinColumns({
				@JoinColumn(name = "author_id", referencedColumnName = "author_id", nullable = false),
				@JoinColumn(name = "task_list_id", referencedColumnName = "task_list_id", nullable = false)
		})
		private SubmissionList submissionList;
	}
}
