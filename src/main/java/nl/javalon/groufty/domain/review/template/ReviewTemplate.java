package nl.javalon.groufty.domain.review.template;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.review.ReviewTemplateMismatchException;
import nl.javalon.groufty.domain.review.UnscaledGrade;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.review.instance.ReviewProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * Models the template for writing a {@link Review}. 
 * Includes a list of {@link ReviewTemplateProperty} objects further specifying the template.
 */
@Entity
@Table(name = "review_template",
		indexes = @Index(name = "review_template_name_index", columnList = "name", unique = true)
)
@Getter
@Setter
public class ReviewTemplate {

	@Id
	@GeneratedValue
	@Column(name = "review_template_id", nullable = false)
	private long reviewTemplateId;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@OneToMany(mappedBy = "reviewTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private List<ReviewTemplateProperty> reviewTemplateProperties;

	/**
	 * Calculate a grade based on the provided list of reviewProperties.
	 * @param reviewProperties
	 * @return
	 */
	public BigDecimal calculateGrade(List<ReviewProperty> reviewProperties) throws ReviewTemplateMismatchException {
		if (reviewProperties.size() != reviewTemplateProperties.size())
			throw new ReviewTemplateMismatchException();

		Iterator<ReviewTemplateProperty> templatePropertyIt = this.reviewTemplateProperties.iterator();
		Iterator<ReviewProperty> propertyIt = reviewProperties.iterator();

		UnscaledGrade unscaledGrade = new UnscaledGrade();

		while (templatePropertyIt.hasNext() && propertyIt.hasNext()) {
			ReviewTemplateProperty templateProperty = templatePropertyIt.next();
			ReviewProperty property = propertyIt.next();

			templateProperty.calculateGrade(property, unscaledGrade);
		}

		return unscaledGrade.computeScaledGrade();
	}
}
