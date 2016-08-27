package nl.javalon.groufty.repository.page;

import nl.javalon.groufty.dto.SimplePage;
import nl.javalon.groufty.dto.page.task.SubmissionDetailsDto;
import nl.javalon.groufty.dto.page.task.TeacherSubmissionListExpandDto;
import nl.javalon.groufty.dto.page.task.TeacherSubmissionListOverviewDto;
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
import java.math.BigDecimal;

/**
 * Repository for editor submission list page.
 * @author Lukas Miedema
 */
@Transactional
@Repository
public class TeacherSubmissionListPageRepository extends AbstractNativeRepository {

	private final ResultTransformer
			submissionListOverviewTransformer,
			submissionListExpandTransformer,
			submissionDetailsTransformer;

	@Inject
	public TeacherSubmissionListPageRepository(ConversionService cs) {
		this.submissionListOverviewTransformer = new AnnotatedBeanResultTransformer<>(TeacherSubmissionListOverviewDto.class, cs);
		this.submissionListExpandTransformer = new AnnotatedBeanResultTransformer<>(TeacherSubmissionListExpandDto.class, cs);
		this.submissionDetailsTransformer = new AnnotatedBeanResultTransformer(SubmissionDetailsDto.class, cs);
	}

	public Page<TeacherSubmissionListOverviewDto> getTeacherTaskListOverview(
			long taskListId, BigDecimal minimumDifference, boolean filterMinimumDifference, Pageable page) {
		Query query = prepareQuery("teacher-submissionlist-overview", submissionListOverviewTransformer);
		query.setLong("task_list_id", taskListId);
		query.setBoolean("filter_min_diff", filterMinimumDifference);
		query.setBigDecimal("min_diff", minimumDifference);
		return new SimplePage<>(page, query.list());
	}

	public Page<TeacherSubmissionListExpandDto> getTeacherTaskListExpand(
			long taskListId, long authorId, Pageable page) {

		Query query = prepareQuery("teacher-submissionlist-expand", submissionListExpandTransformer);
		query.setLong("task_list_id", taskListId);
		query.setLong("author_id", authorId);
		return new SimplePage<>(page, query.list());
	}

	public SubmissionDetailsDto getTeacherSubmissionDetails(long authorId, long taskId) {
		Query query = prepareQuery("teacher-submission-details", submissionDetailsTransformer);
		query.setParameter("author_id", authorId);
		query.setParameter("task_id", taskId);
		return (SubmissionDetailsDto) query.uniqueResult();
	}
}
