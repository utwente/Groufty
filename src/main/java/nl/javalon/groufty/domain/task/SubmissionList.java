package nl.javalon.groufty.domain.task;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Delegate;
import nl.javalon.groufty.domain.user.Author;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Represents a connection between a set of {@link Submission}s and a {@link TaskList}
 */
@Entity
@Table(name = "submission_list")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class SubmissionList {

	@Delegate
	@EmbeddedId
	private SubmissionListPk id;

	@JsonIgnore
	@OneToMany(mappedBy = "id.submissionList", orphanRemoval = true)
	private Set<Submission> submissions;

	@Column(name = "override_final_grade")
	private BigDecimal overrideFinalGrade;

	@Column(name = "finalized", nullable = false)
	private boolean finalized = false;

	// JSON derived properties
	@JsonGetter
	public long getAuthorId() {
		return getAuthor().getAuthorId();
	}

	@JsonGetter
	public long getTaskListId() {
		return getTaskList().getTaskListId();
	}

	/**
	 * Primary key for a SubmissionList
	 */
	@Data
	@Embeddable
	public static class SubmissionListPk implements Serializable {

		@ManyToOne
		@JoinColumn(name = "task_list_id", nullable = false)
		@JsonIgnore
		private TaskList taskList;

		@ManyToOne
		@JoinColumn(name = "author_id", nullable = false)
		@JsonIgnore
		private Author author;
	}
}
