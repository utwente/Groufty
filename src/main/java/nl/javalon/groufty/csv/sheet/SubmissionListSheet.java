package nl.javalon.groufty.csv.sheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.javalon.groufty.domain.task.SubmissionList;

import java.math.BigDecimal;

/**
 * @author Lukas Miedema
 */
@Data
@NoArgsConstructor
@JsonPropertyOrder({"taskListName", "authorId", "overrideFinalGrade", "finalized"})
public class SubmissionListSheet {

	// Primary key
	@JsonProperty("Task list name")
	private String taskListName;

	@JsonUnwrapped
	private AuthorIdSheet authorId;

	@JsonProperty("Override final grade")
	private BigDecimal overrideFinalGrade;

	@JsonProperty("Finalized")
	private boolean finalized;

	public SubmissionListSheet(SubmissionList list) {
		setTaskListName(list.getTaskList().getName());
		setAuthorId(new AuthorIdSheet(list.getAuthor()));
		setOverrideFinalGrade(list.getOverrideFinalGrade());
		setFinalized(list.isFinalized());
	}
}
