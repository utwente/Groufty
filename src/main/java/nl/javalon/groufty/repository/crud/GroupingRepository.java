package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.user.Grouping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GroupingRepository extends PagingAndSortingRepository<Grouping, Long> {

	/**
	 * Tests if the provided user is in the provided grouping.
	 * @param userId the id of the user to test
	 * @param groupingId the id of the group to test
	 * @return true if the user is in the group.
	 */
	@Query("SELECT COUNT(g) > 0 FROM Grouping g INNER JOIN g.users u WHERE u.authorId = ?1 AND g.groupingId = ?2")
	boolean groupingContains(long userId, long groupingId);

	/**
	 * Finds a grouping with the provided name.
	 * @param groupingName
	 * @return
	 */
	Grouping findOneByGroupingName(String groupingName);
}
