package nl.javalon.groufty.domain.review;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.task.Submission;
import nl.javalon.groufty.domain.user.Author;
import nl.javalon.groufty.domain.user.Grouping;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Map;
import java.util.Set;

/**
 * Randomly assigns a given number of reviewers to every review from a given group
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class KnownReviewerSelectionStrategy extends ReviewerSelectionStrategy {

	@ManyToOne
	@JoinColumn(name = "grouping_id", nullable = false)
	@JsonIgnore
	private Grouping reviewersSource;

	// Derived properties for JSON
	@JsonGetter
	public long getReviewersSourceGroupingId() {
		return reviewersSource.getGroupingId();
	}

	@Override
	public Map<Submission, Set<Author>> performSelection() {
		throw new UnsupportedOperationException();
	}
}
