package nl.javalon.groufty.dto.task;

import lombok.Data;
import nl.javalon.groufty.domain.review.ReviewerSelectionStrategy;
import nl.javalon.groufty.domain.task.Audience;
import nl.javalon.groufty.domain.task.TaskListState;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Lukas Miedema
 */
@Data
public class TaskListDto {

	private Date startDate;
	private Date submissionDeadline;
	private Date reviewDeadline;
	@NotNull @Length(min=2)	private String name;
	@NotNull private TaskListState state;
	@NotNull private Audience submissionAudience;
	private Long groupingId;
	private ReviewerSelectionStrategy reviewerSelectionStrategy;
}
