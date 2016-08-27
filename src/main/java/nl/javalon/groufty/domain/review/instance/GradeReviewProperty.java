package nl.javalon.groufty.domain.review.instance;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Grade review property
 */
@Entity
@DiscriminatorValue("GRADE")
@Getter
@Setter
public class GradeReviewProperty extends ReviewProperty {

	@Column(name = "grade")
	private BigDecimal grade;
}
