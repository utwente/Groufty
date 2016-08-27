package nl.javalon.groufty.domain.review.instance;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Simple text review property
 */
@Entity
@DiscriminatorValue("TEXT")
@Getter
@Setter
public class TextReviewProperty extends ReviewProperty {

	@Column(name = "text", columnDefinition = "TEXT")
	private String text;
}
