package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Base for User repositories. This is in itself not a repository.
 *
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	/**
	 * Find a user by author id
	 * @param authorId
	 * @return
	 */
	@Override
	User findOne(Long authorId);

	/**
	 * Finds a user by user id
	 * @param id
	 * @return
	 */
	@Query("FROM User u WHERE u.userId = ?1")
	User findOneById(UserId id);

	@Override
	boolean exists(Long authorId);

	@Override
	Iterable<User> findAll();

	@Override
	Iterable<User> findAll(Iterable<Long> authorIds);

	@Modifying
	@Override
	void delete(Long authorId);

	@Modifying
	@Query("DELETE FROM User u WHERE u.userId = ?1")
	void deleteById(UserId id);

}
