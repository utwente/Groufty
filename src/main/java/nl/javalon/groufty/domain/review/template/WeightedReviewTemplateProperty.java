package nl.javalon.groufty.domain.review.template;

import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.review.instance.ReviewProperty;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * Common type for review template properties with weight.
 * @author Lukas Miedema
 */
@Entity
@Getter
@Setter
public abstract class WeightedReviewTemplateProperty<P extends ReviewProperty> extends ReviewTemplateProperty<P> {

	@Column(name = "weight", nullable = false)
	@ColumnDefault("0")
	@Min(0)
	private BigDecimal weight = BigDecimal.ZERO;
}
