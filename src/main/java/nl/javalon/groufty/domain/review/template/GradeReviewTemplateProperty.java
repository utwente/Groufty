package nl.javalon.groufty.domain.review.template;

import nl.javalon.groufty.domain.review.ReviewTemplateMismatchException;
import nl.javalon.groufty.domain.review.UnscaledGrade;
import nl.javalon.groufty.domain.review.instance.GradeReviewProperty;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Grade review template property
 */
@Entity
@DiscriminatorValue("GRADE")
public class GradeReviewTemplateProperty extends WeightedReviewTemplateProperty<GradeReviewProperty> {
	/**
	 * Calculates a grade for this part of the review by adding the maximum value and the current value to the
	 * unscaledGrade.
	 *
	 * @param property
	 * @param unscaledGrade
	 * @throws ReviewTemplateMismatchException
	 */
	@Override
	public void calculateGradeImpl(GradeReviewProperty property, UnscaledGrade unscaledGrade)
			throws ReviewTemplateMismatchException {
		BigDecimal grade = property.getGrade();
		if (grade == null) {
			// Take minimum, which is 1
			grade = BigDecimal.ONE;
		} else if (grade.compareTo(BigDecimal.ONE) < 0 || grade.compareTo(BigDecimal.TEN) > 0) {
			throw new ReviewTemplateMismatchException("Grade value out of bounds: " + grade);
		}

		// Max is 10
		unscaledGrade.add(grade, BigDecimal.TEN, this.getWeight());
	}
}
