package nl.javalon.groufty.csv.sheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import nl.javalon.groufty.domain.task.Submission;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Lukas Miedema
 */
@Data
@JsonPropertyOrder({"taskName", "authorId", "lastEdited", "submitted", "computedGrade", "text", "fileName"})
public class SubmissionSheet {

	// Primary key
	@JsonProperty("Task list name")
	private String taskListName;

	@JsonProperty("Task name")
	private String taskName;

	@JsonUnwrapped
	private AuthorIdSheet authorId;
	// End primary key

	@JsonProperty("Last edited")
	private Date lastEdited;

	@JsonProperty("Submitted")
	private boolean submitted;

	@JsonProperty("Computed grade")
	private BigDecimal computedGrade;

	@JsonProperty("Text (optional)")
	private String text;

	@JsonProperty("File name (optional)")
	private String fileName;

	public SubmissionSheet(Submission submission) {
		setTaskListName(submission.getId().getTask().getTaskList().getName());
		setTaskName(submission.getId().getTask().getName());
		setAuthorId(new AuthorIdSheet(submission.getId().getSubmissionList().getAuthor()));
		setLastEdited(submission.getLastEdited());
		setSubmitted(submission.isSubmitted());
		setText(submission.getText());
		setFileName(submission.getFileDetails().getFileName());
		setComputedGrade(submission.getGrade());
	}
}
