package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.task.SubmissionList;
import nl.javalon.groufty.domain.task.TaskList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SubmissionListRepository extends PagingAndSortingRepository<SubmissionList, SubmissionList.SubmissionListPk> {

	@Query("FROM SubmissionList sl WHERE sl.id.taskList.taskListId = ?1 AND sl.id.author.authorId = ?2")
	SubmissionList findOneByPrimaryKey(long taskListId, long authorId);

	/**
	 * Finds all {@link SubmissionList} where there exists at least one submitted {@link nl.javalon.groufty.domain.task.Submission}
	 * for the given {@link TaskList}
	 * @param taskList
	 * @return
	 */
	@Query("FROM SubmissionList sl WHERE sl.id.taskList = ?1 AND EXISTS(\n" +
			"\tFROM Submission s WHERE s.id.submissionList = sl AND s.submitted = true\n" +
			")")
	List<SubmissionList> findSubmittedByTaskList(TaskList taskList);
}
