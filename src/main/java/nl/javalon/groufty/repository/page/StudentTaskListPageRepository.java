package nl.javalon.groufty.repository.page;

import nl.javalon.groufty.dto.SimplePage;
import nl.javalon.groufty.dto.page.task.StudentTaskListExpandDto;
import nl.javalon.groufty.dto.page.task.StudentTaskListOverviewDto;
import nl.javalon.groufty.dto.page.task.SubmissionDetailsDto;
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
 * Contains native query methods for the student tasklist page repository.
 * @author Lukas Miedema
 */
@Transactional
@Repository
public class StudentTaskListPageRepository extends AbstractNativeRepository {

	private final ResultTransformer taskListOverviewTransformer;
	private final ResultTransformer taskListExpandTransformer;
	private final ResultTransformer submissionDetailsTransformer;

	@Inject
	public StudentTaskListPageRepository(ConversionService cs) {
		this.taskListOverviewTransformer = new AnnotatedBeanResultTransformer(StudentTaskListOverviewDto.class, cs);
		this.taskListExpandTransformer = new AnnotatedBeanResultTransformer(StudentTaskListExpandDto.class, cs);
		this.submissionDetailsTransformer = new AnnotatedBeanResultTransformer(SubmissionDetailsDto.class, cs);
	}

	public Page<StudentTaskListOverviewDto> getStudentTaskListOverview(long authorId, Pageable page) {
		Query query = prepareQuery("student-tasklist-overview", taskListOverviewTransformer);
		return new SimplePage<>(page, query.setParameter("author_id", authorId).list());
	}

	public Page<StudentTaskListExpandDto> getStudentTaskListExpand(long authorId, long taskListId, Pageable page) {
		Query query = prepareQuery("student-tasklist-expand", taskListExpandTransformer);
		query.setParameter("author_id", authorId);
		query.setParameter("tasklist_id", taskListId);
		return new SimplePage<>(page, query.list());
	}

	public SubmissionDetailsDto getStudentTaskDetails(long authorId, long taskId) {
		Query query = prepareQuery("student-submission-details", submissionDetailsTransformer);
		query.setParameter("author_id", authorId);
		query.setParameter("task_id", taskId);
		return (SubmissionDetailsDto) query.uniqueResult();
	}
}
