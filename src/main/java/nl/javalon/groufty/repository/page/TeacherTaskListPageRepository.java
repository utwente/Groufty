package nl.javalon.groufty.repository.page;

import nl.javalon.groufty.dto.SimplePage;
import nl.javalon.groufty.dto.page.task.TeacherTaskListExpandDto;
import nl.javalon.groufty.dto.page.task.TeacherTaskListOverviewDto;
import nl.javalon.groufty.repository.AbstractNativeRepository;
import nl.javalon.groufty.util.convert.AnnotatedBeanResultTransformer;
import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Repository for editor task list pages.
 * @author Lukas Miedema
 */
@Transactional
@Repository
public class TeacherTaskListPageRepository extends AbstractNativeRepository {

	private final ResultTransformer taskListOverviewTransformer, taskListExpandTransformer;

	@Inject
	public TeacherTaskListPageRepository(ConversionService cs) {
		this.taskListOverviewTransformer = new AnnotatedBeanResultTransformer<>(TeacherTaskListOverviewDto.class, cs);
		this.taskListExpandTransformer = new AnnotatedBeanResultTransformer<>(TeacherTaskListExpandDto.class, cs);
	}

	public Page<TeacherTaskListOverviewDto> getTeacherTaskListOverview(Pageable page) {
		Query query = prepareQuery("teacher-tasklist-overview", taskListOverviewTransformer);
		return new SimplePage<>(page, query.list());
	}

	public TeacherTaskListOverviewDto getTeacherTaskListDetails(long taskListId) {
		Query query = prepareQuery("teacher-tasklist-details", taskListOverviewTransformer);
		query.setLong("task_list_id", taskListId);
		return (TeacherTaskListOverviewDto) query.uniqueResult();
	}

	public Page<TeacherTaskListExpandDto> getTeacherTaskListExpand(long taskListId, Pageable page) {
		Query query = prepareQuery("teacher-tasklist-expand", taskListExpandTransformer);
		query.setParameter("task_list_id", taskListId);
		return new SimplePage<>(page, query.list());
	}
}
