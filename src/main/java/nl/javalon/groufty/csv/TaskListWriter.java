package nl.javalon.groufty.csv;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import nl.javalon.groufty.domain.review.instance.*;
import nl.javalon.groufty.domain.review.template.*;
import nl.javalon.groufty.domain.task.Audience;
import nl.javalon.groufty.domain.task.Submission;
import nl.javalon.groufty.domain.task.Task;
import nl.javalon.groufty.domain.task.TaskList;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Exports all data from a task list (submissions with their reviews) into a human-manageable CSV sheet.
 * This differs from the other CSV exporters which more or less dump the database.
 * @author Lukas Miedema
 */
@Service
public class TaskListWriter {

	private final CsvMapper csvMapper = new CsvMapper();
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(CsvService.DATE_FORMAT);

	public TaskListWriter() {
		csvMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		csvMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
	}

	public void exportTaskList(TaskList taskList, OutputStream outputStream) throws IOException {

		// Zip all tasks
		ZipOutputStream out = new ZipOutputStream(outputStream);

		for (Task task: taskList.getTasks()) {
			out.putNextEntry(new ZipEntry(CsvWriter.escapeFileName(task.getName()) + ".csv"));
			exportTask(task, out);
			out.closeEntry();
		}

		out.close();
	}

	public void exportTask(Task task, OutputStream out) throws IOException {

		Set<Submission> submissions = task.getSubmissions();
		ReviewTemplate template = task.getReviewTemplate();
		CsvSchema.Builder schemaBuilder = CsvSchema.builder().setUseHeader(true);

		// Don't write reviews if there's no template
		boolean writeReviewContent = template != null;

		// Add basic columns
		String submissionAudience = task.getTaskList().getSubmissionAudience() == Audience.INDIVIDUAL ?
				"s/m-number" : "group name";

		String reviewAudience = task.getTaskList().getSubmissionAudience() == Audience.GROUP ?
				"s/m-number" : "group name";

		schemaBuilder.addColumn("Submission author (" + submissionAudience + ")");
		schemaBuilder.addColumn("Last edited (YYYY/MM/DD hh:mm:ss)");
		schemaBuilder.addColumn("Submitted");
		schemaBuilder.addColumn("Average grade");

		// Gather all submissions and the max number of reviews per submission
		// This decides the columns
		int maxReviews = 0;
		for (Submission submission : submissions) {
			maxReviews = Math.max(submission.getReviews().size(), maxReviews);
		}

		// Get the review template properties
		List<String> reviewLabels = new LinkedList<>();
		if (writeReviewContent) {
			for (ReviewTemplateProperty property : template.getReviewTemplateProperties()) {
				String propertyName = property.getDescription();
				if (property instanceof RubricReviewTemplateProperty) {

					// Write all rubric rows as columns
					RubricReviewTemplateProperty rubricProperty = (RubricReviewTemplateProperty) property;
					for (RubricReviewTemplateProperty.RubricTemplateRow row : rubricProperty.getRows()) {
						reviewLabels.add(propertyName + "/" + row.getLabel());
					}

				} else {

					// For grade and text
					reviewLabels.add(propertyName);
				}
			}
		}

		// Add them to the csv schema
		for (int i = 1; i <= maxReviews; i++) {

			// General review stuff
			schemaBuilder.addColumn(i + " Review author (" + reviewAudience + ")");
			schemaBuilder.addColumn(i + " Last edited (YYYY/MM/DD hh:mm:ss)");
			schemaBuilder.addColumn(i + " Grade");
			schemaBuilder.addColumn(i + " Review flag");

			// Review template stuff (empty if content writing disabled)
			for (String reviewLabel : reviewLabels) {
				schemaBuilder.addColumn(i + " " + reviewLabel);
			}
		}

		// Create a csv thing for this
		CsvSchema schema = schemaBuilder.build();
		SequenceWriter writer = csvMapper.writerFor(String[].class).with(schema).writeValues(out);
		String[] rowBuffer = new String[schema.size()];
		int col = 0;

		// Write every submission
		for (Submission submission: submissions) {

			// General stuff
			rowBuffer[col++] = submission.getId().getSubmissionList().getId().getAuthor().getName();
			rowBuffer[col++] = submission.getLastEdited() == null ? "" : dateFormat.format(submission.getLastEdited());
			rowBuffer[col++] = String.valueOf(submission.isSubmitted());
			rowBuffer[col++] = submission.getGrade() == null ? "" : submission.getGrade().toPlainString();

			// Reviews
			if (writeReviewContent) {
				for (Review review: submission.getReviews()) {

					if (!review.isSubmitted() || review.isDisabled())
						continue;

					rowBuffer[col++] = review.getAuthor().getName();
					rowBuffer[col++] = review.getLastEdited() == null ? "" : dateFormat.format(review.getLastEdited());
					rowBuffer[col++] = review.getGrade() == null ? "" : review.getGrade().toPlainString();
					rowBuffer[col++] = review.getFlag() == null ? "" : review.getFlag().getMessage();

					// Review template things
					if (review.getReviewProperties() == null || review.getReviewProperties().isEmpty()) {

						// No review -> skip
						for (int i = 0; i < reviewLabels.size(); i++) {
							rowBuffer[col++] = "";
						}
					} else {

						// There's a review
						Iterator<ReviewProperty> reviewIt = review.getReviewProperties().iterator();
						Iterator<ReviewTemplateProperty> reviewTempIt = template.getReviewTemplateProperties().iterator();

						while (reviewIt.hasNext() && reviewTempIt.hasNext()) {
							ReviewProperty prop = reviewIt.next();
							ReviewTemplateProperty<?> templateProp = reviewTempIt.next();

							if (templateProp instanceof RubricReviewTemplateProperty) {

								RubricReviewProperty rubricProp = (RubricReviewProperty) prop;
								RubricReviewTemplateProperty rubricTemplateProp = (RubricReviewTemplateProperty) templateProp;

								// Write all columns of the rubric
								List<RubricReviewTemplateProperty.RubricTemplateColumn> header = rubricTemplateProp.getHeader();
								for (RubricReviewProperty.RubricReviewOption option: rubricProp.getSelectedOptions()) {
									rowBuffer[col++] = option.getOption() == null ? null : header.get(option.getOption()).getValue().toPlainString();
								}

							} else if (templateProp instanceof GradeReviewTemplateProperty) {

								BigDecimal grade = ((GradeReviewProperty) prop).getGrade();
								rowBuffer[col++] = grade == null ? null : grade.toPlainString();

							} else if (templateProp instanceof TextReviewTemplateProperty) {
								rowBuffer[col++] = ((TextReviewProperty) prop).getText();
							}

						}

					}

				}
			}

			// Write and reset
			for (; col < rowBuffer.length; col++) {
				rowBuffer[col] = null;
			}
			writer.write(rowBuffer);
			col = 0;
		}
	}

}
