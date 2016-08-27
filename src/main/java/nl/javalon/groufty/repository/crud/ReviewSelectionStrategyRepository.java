package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.review.ReviewerSelectionStrategy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ReviewSelectionStrategyRepository extends PagingAndSortingRepository<ReviewerSelectionStrategy, Long> {

	/**
	 * Find all {@link ReviewerSelectionStrategy}s that have not been executed yet, are primed and past their submission
	 * deadline so they can be executed now.
	 * @return
	 */
	@Query("FROM ReviewerSelectionStrategy s WHERE s.primed = true AND s.executionDate = null AND s.taskList.submissionDeadline < current_timestamp()")
	List<ReviewerSelectionStrategy> getPrimedPastSubmissionDeadline();
}
