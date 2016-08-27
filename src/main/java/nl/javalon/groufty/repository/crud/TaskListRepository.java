package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.task.TaskList;
import nl.javalon.groufty.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TaskListRepository extends PagingAndSortingRepository<TaskList, Long> {
	Page<TaskList> findByAuthor(User emp, Pageable pageable);

	TaskList findByName(String taskListName);
}
