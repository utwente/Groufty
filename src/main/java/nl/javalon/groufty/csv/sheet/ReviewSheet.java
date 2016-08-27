package nl.javalon.groufty.csv.sheet;

import lombok.Data;
import nl.javalon.groufty.domain.review.instance.ReviewFlag;
import nl.javalon.groufty.domain.review.instance.ReviewProperty;
import nl.javalon.groufty.domain.user.UserId;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Not really a "sheet" as it won't be mapped to a CSV file but to JSON and still used as DTO for the database export.
 * @author Lukas Miedema
 */
@Data
public class ReviewSheet {

	private Date lastEdited;
	private boolean submitted;
	private UserId authorId;
	private long submissionTaskId;
	private AuthorIdSheet submissionAuthorId;
	private BigDecimal grade;
	private List<ReviewProperty> reviewProperties;
	private boolean disabled;
	private ReviewFlag flag;
}
