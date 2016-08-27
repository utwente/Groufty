package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.user.Group;
import nl.javalon.groufty.domain.user.Grouping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {

	/**
	 * Tests if the provided user is in the provided group.
	 * @param userAuthorId the id of the user to test
	 * @param groupAuthorId the id of the group to test
	 * @return true if the user is in the group.
	 */
	@Query("SELECT COUNT(g) > 0 FROM Group g INNER JOIN g.users u WHERE u.authorId = ?1 AND g.authorId = ?2")
	boolean groupContains(long userAuthorId, long groupAuthorId);

	/**
	 * Finds a group by name and grouping.
	 * @param name
	 * @param grouping
	 * @return
	 */
	Group findOneByGroupNameAndGrouping(String name, Grouping grouping);
}
