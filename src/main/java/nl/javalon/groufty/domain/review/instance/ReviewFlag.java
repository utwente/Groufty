package nl.javalon.groufty.domain.review.instance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;

/**
 * If a review was poorly written or the receiver otherwise does not agree with it, the review can be flagged
 * by creating an {@link ReviewFlag}. Once created, a {@link ReviewFlag} cannot be destroyed again.
 * @author Lukas Miedema
 */
@Entity
@Table(name = "review_flag")
@Setter
@Getter
public class ReviewFlag {

	@JsonIgnore
	@Id
	private long reviewId;

	@JsonIgnore
	@MapsId
	@OneToOne
	@JoinColumn(name = "review_id", nullable = false)
	private Review review;

	@ApiModelProperty("The text message, for human consumption")
	@Column(name = "message", nullable = false, columnDefinition = "TEXT")
	@NonNull
	private String message;
}
