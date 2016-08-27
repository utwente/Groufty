package nl.javalon.groufty.dto.task;

import lombok.Data;
import nl.javalon.groufty.domain.task.SubmissionContentType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Data Transfer Object for {@link nl.javalon.groufty.domain.task.Task}
 */
@Data
public class TaskDto {

	// References taskList
	@NotNull
	private Long taskListId;

	@NotNull
	@Size(min = 2)
	private String name;

	@NotNull
	private SubmissionContentType contentType;

	private String description;
}
