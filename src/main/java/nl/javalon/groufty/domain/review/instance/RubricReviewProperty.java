package nl.javalon.groufty.domain.review.instance;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Rubric review property.
 * Contains a list of selected options.
 */
@Entity
@DiscriminatorValue("RUBRIC")
@Getter
@Setter
public class RubricReviewProperty extends ReviewProperty {

	@JsonManagedReference
	@OneToMany(mappedBy = "reviewProperty", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy
	@NotNull
	private List<RubricReviewOption> selectedOptions;

	@Entity
	@Table(name = "rubric_option")
	@Getter
	@Setter
	public static class RubricReviewOption {

		@Id
		@GeneratedValue
		@Column(name = "id", nullable = false)
		@JsonIgnore
		private long id;

		@Column(name = "option")
		private Integer option;

		@JsonBackReference
		@ManyToOne
		@JoinColumn(name = "review_property_id", nullable = false)
		private RubricReviewProperty reviewProperty;
	}
}
