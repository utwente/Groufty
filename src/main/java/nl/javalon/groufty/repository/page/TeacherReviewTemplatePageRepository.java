package nl.javalon.groufty.repository.page;

import nl.javalon.groufty.dto.SimplePage;
import nl.javalon.groufty.dto.page.review.TeacherReviewTemplateInfoDto;
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
 * Repository for editor review template pages.
 * @author Lukas Miedema
 */
@Transactional
@Repository
public class TeacherReviewTemplatePageRepository extends AbstractNativeRepository {

	private final ResultTransformer reviewTemplateOverviewTransformer;

	@Inject
	public TeacherReviewTemplatePageRepository(ConversionService cs) {
		this.reviewTemplateOverviewTransformer = new AnnotatedBeanResultTransformer<>(TeacherReviewTemplateInfoDto.class, cs);
	}

	public Page<TeacherReviewTemplateInfoDto> getTeacherReviewTemplateOverview(Pageable page) {
		Query query = prepareQuery("teacher-reviewtemplate-overview", reviewTemplateOverviewTransformer);
		return new SimplePage<>(page, query.list());
	}

	public TeacherReviewTemplateInfoDto getTeacherReviewTemplateDetails(long reviewTemplateId) {
		Query query = prepareQuery("teacher-reviewtemplate-details", reviewTemplateOverviewTransformer);
		query.setLong("review_template_id", reviewTemplateId);
		return (TeacherReviewTemplateInfoDto) query.uniqueResult();
	}
}
