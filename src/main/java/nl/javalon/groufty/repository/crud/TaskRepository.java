package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.task.Task;
import nl.javalon.groufty.domain.task.TaskList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {
	Page<Task> findByTaskList(TaskList taskList, Pageable pageable);

	Task findByName(String name, TaskList taskList);
}
