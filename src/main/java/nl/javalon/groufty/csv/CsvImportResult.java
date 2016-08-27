package nl.javalon.groufty.csv;

import lombok.Data;

/**
 * Reports how many things have been imported.
 * @author Lukas Miedema
 */
@Data
public class CsvImportResult {
	
	private int
			taskCount = 0,
			taskFileCount = 0,
			taskListCount = 0,
			userCount = 0,
			groupCount = 0,
			groupingCount = 0,
			reviewTemplateCount = 0,
			reviewCount = 0,
			submissionCount = 0,
			submissionFileCount = 0,
			submissionListCount = 0,
			reviewSelectionStrategyCount = 0;
}
