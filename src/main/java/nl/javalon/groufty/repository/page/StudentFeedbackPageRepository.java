package nl.javalon.groufty.repository.page;

import lombok.extern.slf4j.Slf4j;
import nl.javalon.groufty.dto.SimplePage;
import nl.javalon.groufty.dto.page.task.StudentTaskListOverviewDto;
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
 * Contains native query methods for the student feedback page repository.
 * @author Lukas Miedema
 */
@Transactional
@Repository
@Slf4j
public class StudentFeedbackPageRepository extends AbstractNativeRepository {

	private final ResultTransformer taskListOverviewTransformer;

	@Inject
	public StudentFeedbackPageRepository(ConversionService cs) {
		this.taskListOverviewTransformer = new AnnotatedBeanResultTransformer(StudentTaskListOverviewDto.class, cs);
	}

	public Page<StudentTaskListOverviewDto> getStudentFeedbackOverview(long authorId, Pageable page) {
		Query query = prepareQuery("student-feedback-overview", taskListOverviewTransformer);
		return new SimplePage<>(page, query.setParameter("author_id", authorId).list());
	}
}
