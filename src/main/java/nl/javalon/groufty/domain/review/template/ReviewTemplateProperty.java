package nl.javalon.groufty.domain.review.template;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.review.ReviewTemplateMismatchException;
import nl.javalon.groufty.domain.review.UnscaledGrade;
import nl.javalon.groufty.domain.review.instance.ReviewProperty;

import javax.persistence.*;
import java.lang.reflect.ParameterizedType;

/**
 * Primitive review template property. Extended by more specific template properties.
 */
@Entity
@Table(name = "review_template_property")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonTypeInfo(property = "type", use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
		@JsonSubTypes.Type(value = GradeReviewTemplateProperty.class, name = "GRADE"),
		@JsonSubTypes.Type(value = TextReviewTemplateProperty.class, name = "TEXT"),
		@JsonSubTypes.Type(value = RubricReviewTemplateProperty.class, name = "RUBRIC")
})
@Getter
@Setter
public abstract class ReviewTemplateProperty<P extends ReviewProperty> {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "review_template_property_id", nullable = false)
	private long reviewTemplatePropertyId;

	@Column(name = "description", nullable = false, columnDefinition = "TEXT")
	private String description;

	@ManyToOne
	@JoinColumn(name = "review_template_id")
	@JsonBackReference
	private ReviewTemplate reviewTemplate;

	/**
	 * Calculates a grade for this part of the review by adding the maximum value and the current value to the
	 * unscaledGrade.
	 * @param property
	 * @param unscaledGrade
	 * @throws ReviewTemplateMismatchException
	 */
	@SuppressWarnings("unchecked")
	public void calculateGrade(ReviewProperty property, UnscaledGrade unscaledGrade) throws ReviewTemplateMismatchException {
		try {
			calculateGradeImpl((P) property, unscaledGrade);
		} catch (ClassCastException e) {
			ParameterizedType p = (ParameterizedType) getClass().getGenericSuperclass();
			Class<?> expectedClass = (Class) p.getActualTypeArguments()[0];

			String expected = expectedClass.getSimpleName();
			String got = property.getClass().getSimpleName();
			throw new ReviewTemplateMismatchException("Template expected " + expected + ", but got " + got);
		}
	}

	protected abstract void calculateGradeImpl(P property, UnscaledGrade unscaledGrade)
			throws ReviewTemplateMismatchException;
}
