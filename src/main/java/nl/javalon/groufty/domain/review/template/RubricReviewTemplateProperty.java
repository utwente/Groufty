package nl.javalon.groufty.domain.review.template;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.groufty.domain.review.ReviewTemplateMismatchException;
import nl.javalon.groufty.domain.review.UnscaledGrade;
import nl.javalon.groufty.domain.review.instance.RubricReviewProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * Rubric review template property. Models a 2D rubric with weights and values and headers.
 */
@Entity
@DiscriminatorValue("RUBRIC")
@Getter
@Setter
public class RubricReviewTemplateProperty extends WeightedReviewTemplateProperty<RubricReviewProperty> {

	@OneToMany(mappedBy = "rubricReviewTemplateProperty", fetch = FetchType.LAZY, orphanRemoval = true,
			cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private List<RubricTemplateColumn> header;

	@OneToMany(mappedBy = "rubricReviewTemplateProperty", fetch = FetchType.LAZY, orphanRemoval = true,
			cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private List<RubricTemplateRow> rows;

	@Override
	protected void calculateGradeImpl(RubricReviewProperty property, UnscaledGrade unscaledGrade)
			throws ReviewTemplateMismatchException {

		List<RubricReviewProperty.RubricReviewOption> selectedOptions = property.getSelectedOptions();
		if (selectedOptions == null || selectedOptions.size() != rows.size()) {
			throw new ReviewTemplateMismatchException("Selected options in rubric do not match review template");
		}

		if (header.isEmpty()) {
			throw new ReviewTemplateMismatchException("Rubric Template has no columns so can't possibly be used");
		}

		if (rows.isEmpty()) {
			throw new ReviewTemplateMismatchException("Rubric Template has no rows");
		}

		// Calculate without taking weight into consideration
		UnscaledGrade rubricGrade = new UnscaledGrade();

		Iterator<RubricReviewProperty.RubricReviewOption> selectedOptionIt = selectedOptions.iterator();
		Iterator<RubricTemplateRow> rowIt = rows.iterator();

		// Find the least favorable column to pick as a default
		RubricTemplateColumn defaultColumn = header.stream().min((a,b) -> a.value.compareTo(b.value)).get();
		RubricTemplateColumn maxColumn = header.stream().max((a,b) -> a.value.compareTo(b.value)).get();

		while (selectedOptionIt.hasNext() && rowIt.hasNext()) {
			RubricReviewProperty.RubricReviewOption selectedOption = selectedOptionIt.next();
			RubricTemplateRow row = rowIt.next();

			RubricTemplateColumn selectedColumn = null;

			if (selectedOption.getOption() == null) {
				selectedColumn = defaultColumn;
			} else {
				// Bounds check
				int option = selectedOption.getOption();
				if (option < 0 || option >= header.size()) {
					throw new ReviewTemplateMismatchException("Rubric option out of bounds for item: " + row.getLabel());
				}
				selectedColumn = header.get(option);
			}
			rubricGrade.add(selectedColumn.getValue(), maxColumn.getValue(), row.getWeight());
		}

		// Add to the real thing
		unscaledGrade.add(rubricGrade, this.getWeight());
	}

	/**
	 * A column (for header) in the 2D rubric
	 */
	@Entity
	@Table(name = "rubric_template_column")
	@Getter @Setter
	public static class RubricTemplateColumn {

		@JsonIgnore
		@Id
		@Column(name = "id")
		@GeneratedValue
		private long id;

		@ManyToOne
		@JoinColumn(name = "review_template_property_id")
		@OrderBy
		@JsonBackReference
		private RubricReviewTemplateProperty rubricReviewTemplateProperty;

		@Column(name = "label", nullable = false, columnDefinition = "TEXT")
		private String label;

		@Column(name = "value", nullable = false)
		private BigDecimal value;
	}

	/**
	 * A row in the 2D rubric + all options on that row
	 */
	@Entity
	@Table(name = "rubric_template_row")
	@Getter @Setter
	public static class RubricTemplateRow {

		@JsonIgnore
		@Id
		@Column(name = "id")
		@GeneratedValue
		private long id;

		@ManyToOne
		@JoinColumn(name = "review_template_property_id")
		@JsonBackReference
		private RubricReviewTemplateProperty rubricReviewTemplateProperty;

		@Column(name = "label", nullable = false, columnDefinition = "TEXT")
		private String label;

		@Column(name = "weight", nullable = false)
		private BigDecimal weight;

		@OneToMany(mappedBy = "row", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
		@Column(name = "option", nullable = false)
		@OrderBy
		@JsonManagedReference
		private List<RubricTemplateCell> options;
	}

	/**
	 * A cell
	 */
	@Entity
	@Table(name = "rubric_template_cell")
	@Getter @Setter
	public static class RubricTemplateCell {

		@Id
		@JsonIgnore
		@Column(name = "id")
		@GeneratedValue
		private long id;

		@ManyToOne
		@JoinColumn(name = "rubric_template_row_id")
		@JsonBackReference
		private RubricTemplateRow row;

		@Column(name = "label", nullable = false, columnDefinition = "TEXT")
		private String label;
	}
}
