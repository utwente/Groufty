package nl.javalon.groufty.dto.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for {@link nl.javalon.groufty.domain.task.SubmissionList}
 * @author Lukas Miedema
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SubmissionListDto {

	@ApiModelProperty("Provide value to provide an 'override' grade for this SubmissionList")
	private BigDecimal overrideFinalGrade;

	@ApiModelProperty("Set to true to finalize the SubmissionList. Only then the finalGrade property will be stored")
	private boolean finalized;
}
