package nl.javalon.groufty.domain.review.instance;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.review.template.GradeReviewTemplateProperty;
import nl.javalon.groufty.domain.review.template.ReviewTemplateProperty;
import nl.javalon.groufty.domain.review.template.RubricReviewTemplateProperty;
import nl.javalon.groufty.domain.review.template.TextReviewTemplateProperty;

import javax.persistence.*;

/**
 * Primitive review property. Extended by more specific review properties.
 */
@Entity
@Table(name = "review_property")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonTypeInfo(property = "type", use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
		@JsonSubTypes.Type(value = GradeReviewProperty.class, name = "GRADE"),
		@JsonSubTypes.Type(value = TextReviewProperty.class, name = "TEXT"),
		@JsonSubTypes.Type(value = RubricReviewProperty.class, name = "RUBRIC")
})
@Getter
@Setter
public abstract class ReviewProperty {

	@Id
	@GeneratedValue
	@Column(name = "review_property_id")
	@JsonIgnore
	private long reviewPropertyId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id", nullable = false)
	@JsonIgnore
	private Review review;
}
