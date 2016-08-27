package nl.javalon.groufty.dto.page.review;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import nl.javalon.groufty.util.convert.SqlToBeanConstructor;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author Lukas Miedema
 */
@Data
@RequiredArgsConstructor(onConstructor = @__(@SqlToBeanConstructor))
public class ReviewOverviewDto {

	private final BigInteger taskListId;
	private final BigInteger taskId;

	private final String taskListName;
	private final String taskName;
	private final Boolean showGradesToReviewers;

	private final Date reviewDeadline;
	private final String taskTarget;
	private final Boolean anonymousReview;

	private final BigInteger totalReviewCount;
	private final BigInteger submittedReviewCount;

	private final Date lastEdited;
}
