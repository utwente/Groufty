package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.task.Submission;
import nl.javalon.groufty.domain.task.TaskList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SubmissionRepository extends PagingAndSortingRepository<Submission, Submission.SubmissionPk> {

	@Query("FROM Submission s WHERE s.id.task.taskId = ?1 AND s.id.submissionList.id.author.authorId = ?2")
	Submission findOneByPrimaryKey(long taskId, long authorId);

	@Query("SELECT r.submission FROM Review r WHERE r.reviewId = ?1")
	Submission findByReviewId(long reviewId);

	/**
	 * Retrieve all submitted submissions by task list.
	 * @param taskList
	 * @return
	 */
	@Query("FROM Submission s WHERE s.submitted = true AND s.id.submissionList.id.taskList = ?1")
	List<Submission> findSubmittedByTaskList(TaskList taskList);
}
