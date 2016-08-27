package nl.javalon.groufty.domain.task;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.review.template.ReviewTemplate;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.domain.util.FileDetails;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Represents a single task in the system.
 */
@Entity
@Table(name = "task",
		indexes = @Index(name = "task_name_index", columnList = "name,task_id", unique = true)
)
@Getter
@Setter
@EqualsAndHashCode(of = "taskId")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "task_id", nullable = false)
	@NotNull
	private long taskId;

	@Column(name = "task_order")
	private Long order; // default is sorted on order

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_list_id", nullable = false)
	private TaskList taskList;

	@Column(name = "name", nullable = false)
	@NotNull
	private String name;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	private User author;

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_template_id")
	private ReviewTemplate reviewTemplate;

	@JsonIgnore
	@OneToMany(mappedBy = "id.task")
	private Set<Submission> submissions;

	@Embedded
	private FileDetails fileDetails; // optional, if a file is included

	@Enumerated(EnumType.STRING)
	@Column(name = "content_type", nullable = false)
	private SubmissionContentType contentType;

	@Column(name = "show_grades_to_reviewers", nullable = false)
	@ColumnDefault("true")
	private boolean showGradesToReviewers;

	// Derived properties
	@JsonGetter
	public long getTaskListId() {
		return taskList.getTaskListId();
	}

	@JsonGetter
	public UserId getAuthorId() {
		return author.getUserId();
	}

	@JsonGetter
	public Long getReviewTemplateId() {
		return reviewTemplate == null ? null : reviewTemplate.getReviewTemplateId();
	}

	@JsonIgnore
	public String getReviewTemplateName() {
		return reviewTemplate == null ? null : reviewTemplate.getName();
	}
}

