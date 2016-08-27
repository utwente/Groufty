package nl.javalon.groufty.dto.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * DTO for {@link nl.javalon.groufty.domain.task.Submission}
 */
@Data
public class SubmissionDto {

	@ApiModelProperty("When set will convert this submission to a TEXT_SUBMISSION and delete any file.")
	private String text;

	@NotNull
	private boolean submitted;
}
