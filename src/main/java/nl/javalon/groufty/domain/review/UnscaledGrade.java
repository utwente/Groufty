package nl.javalon.groufty.domain.review;

import lombok.Data;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Mutable class containing a grade and a maximum grade. This makes it "unscaled" in the sense that it's not a
 * nice value between 1 and 10. This is an intermediary class used in evaluating reviews.
 * @author Lukas Miedema
 */
@Data
public class UnscaledGrade {

	public static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.UP);

	private BigDecimal grade = BigDecimal.ZERO;
	private BigDecimal max = BigDecimal.ZERO;

	/**
	 * Returns a grade with a value between 0 and 10.
	 * @return
	 */
	public BigDecimal computeScaledGrade() {
		return this.grade.multiply(BigDecimal.TEN, MathContext.UNLIMITED).divide(max, MATH_CONTEXT);
	}

	/**
	 * Adds the supplied grade and weight to this instance.
	 * @param grade
	 * @param weight
	 */
	public void add(BigDecimal grade, BigDecimal max, BigDecimal weight) {
		this.grade = this.grade.add(grade.multiply(weight, MathContext.UNLIMITED).divide(max, MATH_CONTEXT));
		this.max = this.max.add(weight);
	}

	/**
	 * Adds the provided unscaled grade using the provided weight.
	 * @param grade
	 * @param weight
	 */
	public void add(UnscaledGrade grade, BigDecimal weight) {
		this.add(grade.grade, grade.max, weight);
	}
}
