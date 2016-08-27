package nl.javalon.groufty.csv;

import lombok.Data;

/**
 * All url parameters send in an export request dictating which things to export.
 * @author Lukas Miedema
 */
@Data
public class CsvExportRequest {

	private boolean
			exportTasks = true,
			exportTaskFiles = true,
			exportTaskLists = true,
			exportUsers = true,
			exportGroups = true,
			exportGroupings = true,
			exportReviewTemplates = true,
			exportReviews = true,
			exportSubmissions = true,
			exportSubmissionFiles = true,
			exportSubmissionLists = true,
			exportReviewSelectionStrategy = true;
}
