package nl.javalon.groufty.domain.review.template;

import nl.javalon.groufty.domain.review.ReviewTemplateMismatchException;
import nl.javalon.groufty.domain.review.UnscaledGrade;
import nl.javalon.groufty.domain.review.instance.TextReviewProperty;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Simple text review template property
 */
@Entity
@DiscriminatorValue("TEXT")
public class TextReviewTemplateProperty extends ReviewTemplateProperty<TextReviewProperty> {
	/**
	 * Calculates a grade for this part of the review by adding the maximum value and the current value to the
	 * unscaledGrade.
	 *
	 * @param property
	 * @param unscaledGrade
	 * @throws ReviewTemplateMismatchException
	 */
	@Override
	public void calculateGradeImpl(TextReviewProperty property, UnscaledGrade unscaledGrade)
			throws ReviewTemplateMismatchException {
		// Nothing to calculate for a Text property
	}
}
