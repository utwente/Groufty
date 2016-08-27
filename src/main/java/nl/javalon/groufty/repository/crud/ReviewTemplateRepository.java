package nl.javalon.groufty.repository.crud;

import nl.javalon.groufty.domain.review.template.ReviewTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReviewTemplateRepository extends PagingAndSortingRepository<ReviewTemplate, Long> {
	ReviewTemplate findByName(String reviewTemplateName);
}
