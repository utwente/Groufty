package nl.javalon.groufty.csv.sheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.javalon.groufty.domain.task.SubmissionContentType;
import nl.javalon.groufty.domain.task.Task;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.domain.util.FileDetails;

/**
 * @author Lukas Miedema
 */
@JsonPropertyOrder({"taskListName", "taskName", "fileName", "description", "authorId", "contentType", "reviewTemplateName", "showGradesToReviewers"})
@Data
@NoArgsConstructor
public class TaskSheet {

	public TaskSheet(Task task) {
		setTaskListName(task.getTaskList().getName());
		setTaskName(task.getName());
		FileDetails details = task.getFileDetails();
		setFileName(details == null ? null : details.getFileName());
		setDescription(task.getDescription());
		setAuthorId(task.getAuthorId());
		setContentType(task.getContentType());
		setReviewTemplateName(task.getReviewTemplateName());
		setShowGradeToReviewers(task.isShowGradesToReviewers());
	}

	@JsonProperty("Task list name")
	private String taskListName;

	@JsonProperty("Task name")
	private String taskName;

	@JsonProperty("File name (optional)")
	private String fileName;

	@JsonProperty("Description")
	private String description;

	@JsonProperty("Author")
	private UserId authorId;

	@JsonProperty("Submission content type")
	private SubmissionContentType contentType;

	@JsonProperty("Review template name")
	private String reviewTemplateName;

	@JsonProperty("Show review grade to reviewers")
	boolean showGradeToReviewers;
}
