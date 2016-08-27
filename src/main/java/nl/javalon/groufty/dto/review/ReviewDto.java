package nl.javalon.groufty.dto.review;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;
import nl.javalon.groufty.domain.review.instance.ReviewProperty;

import java.util.List;

/**
 * @author Lukas Miedema
 */
@Data
public class ReviewDto {

	@ApiModelProperty("Id of the author of this review")
	@NonNull private Boolean submitted;
	@NonNull private List<ReviewProperty> reviewProperties;
}
