package nl.javalon.groufty.csv;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import nl.javalon.groufty.csv.sheet.*;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.review.template.ReviewTemplate;
import nl.javalon.groufty.domain.task.Submission;
import nl.javalon.groufty.domain.task.SubmissionList;
import nl.javalon.groufty.domain.task.Task;
import nl.javalon.groufty.domain.task.TaskList;
import nl.javalon.groufty.domain.user.Author;
import nl.javalon.groufty.domain.user.User;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Writes and zips database content to CSV files.
 * @author Lukas Miedema
 */
public class CsvWriter {

	public static final String SAFE_FILENAME_REGEX = "[^\\w \\-,.()$_]";
	public static final String PDF_FILE_EXTENSION = ".pdf";
	public static final String JSON_FILE_EXTENSION = ".json";

	private final CsvService service;
	private final ObjectWriter userWriter, groupingWriter, groupWriter, taskListWriter, taskWriter,
			reviewTemplateWriter, reviewPropertyListWriter, submissionListWriter, submissionWriter;

	// CSV suppliers
	private final Supplier<Stream<User>> userSupplier;
	private final Supplier<Stream<GroupingSheet>> groupingSupplier;
	private final Supplier<Stream<GroupSheet>> groupSupplier;
	private final Supplier<Stream<TaskSheet>> taskSupplier;
	private final Supplier<Stream<TaskListSheet>> taskListSupplier;
	private final Supplier<Stream<SubmissionListSheet>> submissionListSupplier;
	private final Supplier<Stream<SubmissionSheet>> submissionSupplier;

	// File suppliers
	private final Supplier<Stream<FileInfo>> taskFileSupplier;
	private final Supplier<Stream<FileInfo>> submissionFileSupplier;
	private final Supplier<Stream<FileInfo>> reviewTemplateSupplier;
	private final Supplier<Stream<FileInfo>> reviewPropertiesSupplier;

	/**
	 * Constructor to be called by {@link CsvService}
	 * @param service
	 */
	CsvWriter(CsvService service) {
		this.service = service;

		// Create CSV writers
		this.userWriter = createCsvWriter(User.class);
		this.groupWriter = createCsvWriter(GroupSheet.class);
		this.groupingWriter = createCsvWriter(GroupingSheet.class);
		this.taskListWriter = createCsvWriter(TaskListSheet.class);
		this.taskWriter = createCsvWriter(TaskSheet.class);
		this.submissionListWriter = createCsvWriter(SubmissionListSheet.class);
		this.submissionWriter = createCsvWriter(SubmissionSheet.class);

		// Create JSON writers
		this.reviewTemplateWriter = service.jsonMapper.writerFor(ReviewTemplate.class);
		this.reviewPropertyListWriter = service.jsonMapper.writerFor(Review.class);

		// CSV suppliers
		this.userSupplier = () -> stream(service.userRepository.findAll());
		this.groupingSupplier = () -> stream(service.groupingRepository.findAll())
				.flatMap(g -> g.getUsers()
						.stream().map(u -> new GroupingSheet(g.getGroupingName(), u.getUserId())));
		this.groupSupplier = () -> stream(service.groupRepository.findAll())
				.flatMap(g -> g.getUsers()
						.stream().map(u -> new GroupSheet(g.getGroupName(), g.getGrouping().getGroupingName(), u.getUserId())));
		this.taskSupplier = () -> stream(service.taskRepository.findAll()).map(TaskSheet::new);
		this.taskListSupplier = () -> stream(service.taskListRepository.findAll()).map(TaskListSheet::new);
		this.submissionListSupplier = () -> stream(service.submissionListRepository.findAll()).map(SubmissionListSheet::new);
		this.submissionSupplier = () -> stream(service.submissionRepository.findAll()).map(SubmissionSheet::new);

		// File suppliers
		this.taskFileSupplier = () -> stream(service.taskRepository.findAll())
				.map(this::fileInfoFromTask).filter(Objects::nonNull);
		this.submissionFileSupplier = () -> stream(service.submissionRepository.findAll())
				.map(this::fileInfoFromSubmission).filter(Objects::nonNull);

		// JSON-to-file suppliers
		this.reviewTemplateSupplier = () -> stream(service.reviewTemplateRepository.findAll())
				.map(this::fileInfoFromReviewTemplate);
		this.reviewPropertiesSupplier = () -> stream(service.reviewRepository.findAll())
				.map(this::fileInfoFromReview);
	}

	/**
	 * Utility for creating CSV writers
	 * @param clazz
	 * @return
	 */
	private ObjectWriter createCsvWriter(Class<?> clazz) {
		CsvSchema schema = service.csvMapper.typedSchemaFor(clazz).withStrictHeaders(true).withUseHeader(true);
		return service.csvMapper.writerFor(clazz).with(schema);
	}

	/**
	 * Exports all CSV in a zip.
	 * @param request
	 * @return
	 */
	public void write(CsvExportRequest request, OutputStream outputStream) throws IOException, SQLException {
		ZipOutputStream zip = new ZipOutputStream(outputStream);

		// Store normal CSV, conditionally
		conditionalZipCsv(request.isExportUsers(), zip, "Users", userWriter, userSupplier);
		conditionalZipCsv(request.isExportGroups(), zip, "Groups", groupWriter, groupSupplier);
		conditionalZipCsv(request.isExportGroupings(), zip, "Groupings", groupingWriter, groupingSupplier);
		conditionalZipCsv(request.isExportTasks(), zip, "Tasks", taskWriter, taskSupplier);
		conditionalZipCsv(request.isExportTaskLists(), zip, "Task lists", taskListWriter, taskListSupplier);
		conditionalZipCsv(request.isExportSubmissionLists(), zip, "Submission lists", submissionListWriter, submissionListSupplier);
		conditionalZipCsv(request.isExportSubmissions(), zip, "Submissions", submissionWriter, submissionSupplier);

		// Store files
		conditionalZipFiles(request.isExportTaskFiles(), zip, "Task files", taskFileSupplier);
		conditionalZipFiles(request.isExportSubmissionFiles(), zip, "Submission files", submissionFileSupplier);
		conditionalZipFiles(request.isExportReviewTemplates(), zip, "Review templates", reviewTemplateSupplier);
		conditionalZipFiles(request.isExportReviews(), zip, "Reviews", reviewPropertiesSupplier);

		// Close and finish
		zip.close();
	}

	/**
	 * Create parameterized stream for an iterable. Very useful for streaming whatever Hibernate returns.
	 * @param iterable
	 * @param <T>
	 * @return
	 */
	private <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	/**
	 * Write something to zip only if the condition is true
	 * @param condition
	 * @param zip
	 * @param name
	 * @param writer
	 * @param entitySource
	 * @param <T>
	 * @throws IOException
	 */
	private <T> void conditionalZipCsv(boolean condition, ZipOutputStream zip, String name,
	                                   ObjectWriter writer, Supplier<Stream<T>> entitySource) throws IOException {
		if (condition) {
			ZipEntry entry = new ZipEntry(name + ".csv");
			zip.putNextEntry(entry);

			SequenceWriter sequenceWriter = writer.writeValues(zip);
			Iterator<T> it = entitySource.get().iterator();
			while (it.hasNext()) {
				sequenceWriter.write(it.next());
			}
		}
	}

	/**
	 * Write something to zip only if the condition is true
	 * @param condition
	 * @param zip
	 * @param folder
	 * @param fileDetails
	 * @throws IOException
	 * @throws SQLException
	 */
	private void conditionalZipFiles(boolean condition, ZipOutputStream zip, String folder,
	                                 Supplier<Stream<FileInfo>> fileDetails) throws IOException, SQLException {
		if (condition) {
			Stream<FileInfo> fileDetailsStream = fileDetails.get();
			Iterator<FileInfo> it = fileDetailsStream.iterator();
			while (it.hasNext()) {
				FileInfo info = it.next();

				// Write the file
				zip.putNextEntry(new ZipEntry(folder + "/" + info.fileName));
				info.writer.write(zip);
			}
		}
	}

	/**
	 * Removes all illegal characters in a file name
	 * @param fileName
	 * @return
	 */
	public static String escapeFileName(String fileName) {
		return fileName.replaceAll(SAFE_FILENAME_REGEX, "");
	}

	/**
	 * Creates file info for a task's file, or null if the task has no file.
	 * @param task
	 * @return
	 */
	private FileInfo fileInfoFromTask(Task task) {
		if (task.getFileDetails() != null) {
			String fileName = escapeFileName(task.getName()) + PDF_FILE_EXTENSION;
			String folderName = escapeFileName(task.getTaskList().getName());
			try {
				return new FileInfo(folderName + "/" + fileName, task.getFileDetails().getFile().getBinaryStream());
			} catch (SQLException e) {
				throw new CsvException(e);
			}
		}
		return null; // no file
	}

	/**
	 * Creates a file info for the submissions' file, or null.
	 * @param submission
	 * @return
	 */
	private FileInfo fileInfoFromSubmission(Submission submission) {
		if (submission.getFileDetails() != null) {
			Task task = submission.getId().getTask();
			TaskList taskList = task.getTaskList();
			SubmissionList submissionList = submission.getId().getSubmissionList();

			String fileName = escapeFileName(submissionList.getAuthor().getName()) + PDF_FILE_EXTENSION;
			String folderName = escapeFileName(taskList.getName()) + "/" + escapeFileName(task.getName());
			try {
				return new FileInfo(folderName + "/" + fileName,
						submission.getFileDetails().getFile().getBinaryStream());
			} catch (SQLException e) {
				throw new CsvException(e);
			}
		}
		return null; // no file
	}

	/**
	 * Serializes the review properties to JSON and provides it as FileInfo
	 * @param review
	 * @return
	 */
	private FileInfo fileInfoFromReview(Review review) {
		Submission submission = review.getSubmission();
		Task task = submission.getId().getTask();
		TaskList taskList = task.getTaskList();
		Author author = submission.getId().getSubmissionList().getAuthor();

		// Full name is <task list> / <task> / <submission author> / <review author>.json
		String folderName = escapeFileName(taskList.getName()) + "/" + escapeFileName(task.getName()) + "/" +
				escapeFileName(author.getName());
		String fileName = escapeFileName(review.getAuthor().getName()) + JSON_FILE_EXTENSION;
		return new FileInfo(folderName + "/" + fileName,
				o -> reviewPropertyListWriter.writeValue(o, review));
	}

	/**
	 * Serializes a review template to JSON and provides it as FileInfo object.
	 * @param reviewTemplate
	 * @return
	 */
	private FileInfo fileInfoFromReviewTemplate(ReviewTemplate reviewTemplate) {
		// Serialize the json template
		String fileName = reviewTemplate.getName() + JSON_FILE_EXTENSION;
		return new FileInfo(fileName, o -> reviewTemplateWriter.writeValue(o, reviewTemplate));
	}

	/**
	 * Wrapper class for the file name that should be written + the input stream of the file.
	 */
	private static class FileInfo {

		private final String fileName;
		private final OutputStreamWriter writer;

		public FileInfo(String fileName, InputStream inputStream) {
			this(fileName, o -> IOUtils.copy(inputStream, o));
		}

		public FileInfo(String fileName, OutputStreamWriter writer) {
			this.fileName = fileName;
			this.writer = writer;
		}

		@FunctionalInterface
		private interface OutputStreamWriter {
			void write(OutputStream stream) throws IOException;
		}
	}

}
