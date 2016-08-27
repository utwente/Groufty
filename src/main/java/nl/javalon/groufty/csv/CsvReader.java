package nl.javalon.groufty.csv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import nl.javalon.groufty.csv.sheet.*;
import nl.javalon.groufty.domain.review.RandomReviewerSelectionStrategy;
import nl.javalon.groufty.domain.review.ReviewTemplateMismatchException;
import nl.javalon.groufty.domain.review.ReviewerSelectionStrategy;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.review.template.ReviewTemplate;
import nl.javalon.groufty.domain.task.Submission;
import nl.javalon.groufty.domain.task.SubmissionList;
import nl.javalon.groufty.domain.task.Task;
import nl.javalon.groufty.domain.task.TaskList;
import nl.javalon.groufty.domain.user.*;
import nl.javalon.groufty.domain.util.FileDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Reads CSV file and writes content to database.
 * @author Lukas Miedema
 */
public class CsvReader {

	private final CsvService service;
	private final ObjectReader userReader, groupingReader, groupReader, taskListReader, taskReader, submissionReader,
			submissionListReader;
	private final ObjectReader reviewReader, reviewTemplateReader;

	/**
	 * To be invoked by {@link CsvService}
	 * @param service
	 */
	CsvReader(CsvService service) {
		this.service = service;

		// Create readers for CSV
		this.userReader = createCsvReader(User.class);
		this.groupingReader = createCsvReader(GroupingSheet.class);
		this.groupReader = createCsvReader(GroupSheet.class);
		this.taskListReader = createCsvReader(TaskListSheet.class);
		this.taskReader = createCsvReader(TaskSheet.class);
		this.submissionReader = createCsvReader(SubmissionSheet.class);
		this.submissionListReader = createCsvReader(SubmissionListSheet.class);

		// Create readers for JSON
		this.reviewTemplateReader = createJsonReader(ReviewTemplate.class);
		this.reviewReader = createJsonReader(ReviewSheet.class);
	}

	/**
	 * Utility method for creating CSV readers.
	 * @param clazz
	 * @return
	 */
	private ObjectReader createCsvReader(Class<?> clazz) {
		CsvSchema schema = service.csvMapper.typedSchemaFor(clazz).withUseHeader(true);
		return service.csvMapper.readerFor(clazz).with(schema);
	}

	/**
	 * Utility method for creating JSON readers.
	 * @param clazz
	 * @return
	 */
	private ObjectReader createJsonReader(Class<?> clazz) {
		return service.jsonMapper.readerFor(clazz);
	}

	/**
	 * Imports the provided {@link CsvDataSet}
	 * @param csvDataSet
	 */
	public CsvImportResult read(CsvDataSet csvDataSet) throws IOException, CsvException, SQLException, ReviewTemplateMismatchException {

		// Parse CSV
		List<User> users = readMultipartFile(userReader, csvDataSet.getUsers());
		List<GroupingSheet> groupings = readMultipartFile(groupingReader, csvDataSet.getGroupings());
		List<GroupSheet> groups = readMultipartFile(groupReader, csvDataSet.getGroups());
		List<TaskListSheet> taskLists = readMultipartFile(taskListReader, csvDataSet.getTaskLists());
		List<TaskSheet> tasks = readMultipartFile(taskReader, csvDataSet.getTasks());
		List<SubmissionSheet> submissions = readMultipartFile(submissionReader, csvDataSet.getSubmissions());
		List<SubmissionListSheet> submissionLists = readMultipartFile(submissionListReader, csvDataSet.getSubmissionLists());

		// Parse JSON
		List<ReviewTemplate> reviewTemplates = readMultipartFileSet(reviewTemplateReader, csvDataSet.getReviewTemplates());
		List<ReviewSheet> reviews = readMultipartFileSet(reviewReader, csvDataSet.getReviews());

		// Create indexed file pool
		Map<String, MultipartFile> taskFiles = indexFiles(csvDataSet.getTaskFiles());
		Map<String, MultipartFile> submissionFiles = indexFiles(csvDataSet.getSubmissionFiles());

		// Users can be persisted as-is
		service.userRepository.save(users);

		// Persist groupings
		for (GroupingSheet sheet: groupings) {

			// Find domain objects
			User user = findOrCreateUser(sheet.getUserId());
			Grouping grouping = findOrCreateGrouping(sheet.getName());

			// Add
			grouping.getUsers().add(user);
		}

		// Persist groups
		for (GroupSheet sheet: groups) {

			// Find domain objects
			User user = findOrCreateUser(sheet.getUserId());
			Grouping grouping = findOrCreateGrouping(sheet.getGroupingName());
			Group group = findOrCreateGroup(sheet.getGroupName(), grouping);

			// Add
			grouping.getUsers().add(user);
			group.getUsers().add(user);
			group.setGrouping(grouping);
		}

		// Review templates need a bit of post-processing to ensure merging
		for (ReviewTemplate template: reviewTemplates) {
			// Check if the template already exists. If it does, overwrite it
			ReviewTemplate existing = service.reviewTemplateRepository.findByName(template.getName());
			if (existing != null) {
				// copy id. This will make the entity "not new", causing #save to merge instead of persist
				template.setReviewTemplateId(existing.getReviewTemplateId());
			}
			// persist/merge template
			template = service.reviewTemplateRepository.save(template);

			if (existing != null) {
				// this template already existed, so there might be reviews
				// recalculate all review grades
				List<Review> dependantReviews = service.reviewRepository.findByReviewTemplate(template);
				for (Review dependantReview : dependantReviews) {

					// If this throws an exception, just let it propagate up
					// That probably means this review template changed more than the signature
					dependantReview.recalculateGrade();
				}
			}
		}

		// Persist task lists (without tasks)
		for (TaskListSheet sheet : taskLists) {

			// Deserialize review strategy
			ReviewerSelectionStrategy strategy = null;
			TaskListSheet.ReviewSelectionStrategySheet strategySheet = sheet.getReviewSelectionStrategy();
			if (strategySheet != null) {

				// Set impl-specific options
				switch (strategySheet.getReviewSelectionStrategy()) {
					case "random":
						RandomReviewerSelectionStrategy s = new RandomReviewerSelectionStrategy();
						s.setSkipSubmissionlessAuthors(strategySheet.isSkipSubmissionlessAuthors());
						strategy = s;
						break;
					default:
						throw new CsvException("Unrecognised review selection strategy: " +
								strategySheet.getReviewSelectionStrategy());
				}

				// Set common options
				strategy.setReviewCount(strategySheet.getReviewsPerTask());
				strategy.setFrom(service.groupingRepository.findOneByGroupingName(strategySheet.getFromGrouping()));
				strategy.setReviewAudience(strategySheet.getReviewAudience());
				strategy.setPrimed(true);
			}

			// Deserialize task list
			TaskList taskList = new TaskList();
			taskList.setName(sheet.getName());
			taskList.setState(sheet.getState());
			taskList.setStartDate(sheet.getStartDate());
			taskList.setSubmissionDeadline(sheet.getSubmissionDeadline());
			taskList.setReviewDeadline(sheet.getReviewDeadline());
			taskList.setAnonymousReviews(sheet.isAnonymousReviews());
			taskList.setAnonymousSubmissions(sheet.isAnonymousSubmissions());
			taskList.setAuthor(findOrCreateUser(sheet.getAuthorUserId()));
			taskList.setSubmissionAudience(sheet.getSubmissionAudience());
			taskList.setGrouping(findOrCreateGrouping(sheet.getGroupingName()));

			// And persist
			taskList = service.taskListRepository.save(taskList);

			// Strategy can only be set on a managed entity
			if (strategy != null) {
				strategy.setTaskList(taskList);
				taskList.setReviewerSelectionStrategy(strategy);
			}
		}

		// Persist tasks
		for (TaskSheet sheet: tasks) {

			// Find domain objects
			TaskList taskList = service.taskListRepository.findByName(sheet.getTaskListName());
			if (taskList == null) {
				throw new CsvException("Could not store task " + sheet.getTaskName() +
						". No such task list: " + sheet.getTaskListName());
			}

			Task task = new Task();
			task.setAuthor(findOrCreateUser(sheet.getAuthorId()));
			task.setTaskList(taskList);
			task.setName(sheet.getTaskName());
			task.setDescription(sheet.getDescription());
			task.setShowGradesToReviewers(sheet.isShowGradeToReviewers());
			task.setContentType(sheet.getContentType());
			// review template can be null, which simply means none will be set.
			task.setReviewTemplate(service.reviewTemplateRepository.findByName(sheet.getReviewTemplateName()));

			// Find file
			String fileName = sheet.getFileName();
			if (fileName == null || !fileName.isEmpty()) {
				MultipartFile file = taskFiles.get(fileName);
				if (file == null) {
					throw new CsvException("Could not find referenced task file '" + fileName + "' in uploaded task files");
				}
				FileDetails fileDetails = service.fileService.convertFile(file);
				task.setFileDetails(fileDetails);
			}

			// And persist
			service.taskRepository.save(task);
		}

		// Persist submission lists
		for (SubmissionListSheet sheet: submissionLists) {
			TaskList taskList = service.taskListRepository.findByName(sheet.getTaskListName());
			if (taskList == null) {
				throw new CsvException("Submission list refers to non-existing task list: " + sheet.getTaskListName());
			}
			Author author = findOrCreateAuthor(sheet.getAuthorId());
			SubmissionList submissionList = findOrCreateSubmissionList(author, taskList);
			submissionList.setOverrideFinalGrade(sheet.getOverrideFinalGrade());
			submissionList.setFinalized(sheet.isFinalized());
		}

		// Persist submissions
		for (SubmissionSheet sheet: submissions) {
			TaskList taskList = service.taskListRepository.findByName(sheet.getTaskListName());
			if (taskList == null)
				throw new CsvException("Could not find task list " + sheet.getTaskListName() + " for submission");
 			Task task = service.taskRepository.findByName(sheet.getTaskName(), taskList);
			if (task == null) {
				throw new CsvException("Could not find task " + sheet.getTaskName() + " for submission");
			}
			Author author = findOrCreateAuthor(sheet.getAuthorId());
			SubmissionList submissionList = findOrCreateSubmissionList(author, task.getTaskList());
			Submission submission = findOrCreateSubmission(submissionList, task);
			submission.setLastEdited(sheet.getLastEdited());
			submission.setSubmitted(sheet.isSubmitted());
			submission.setText(sheet.getText());

			String fileName = sheet.getFileName();
			if (fileName != null && !fileName.isEmpty()) {
				MultipartFile file = submissionFiles.get(fileName);
				if (file == null) {
					throw new CsvException("Could not find referenced submission file " + fileName + " in uploaded submission files");
				}
				FileDetails fileDetails = service.fileService.convertFile(file);
				submission.setFileDetails(fileDetails);
			}
		}

		// Return row counts
		CsvImportResult result = new CsvImportResult();
		result.setTaskCount(tasks.size());
		result.setTaskFileCount(taskFiles.size());
		result.setTaskListCount(taskLists.size());
		result.setUserCount(users.size());
		result.setGroupCount(groups.size());
		result.setGroupingCount(groupings.size());
		result.setReviewTemplateCount(reviewTemplates.size());
		result.setReviewCount(reviews.size());
		result.setSubmissionCount(submissions.size());
		result.setSubmissionListCount(submissionLists.size());
		result.setSubmissionFileCount(submissionFiles.size());
		result.setReviewSelectionStrategyCount(0);
		return result;
	}

	/**
	 * Attempt to find the submission. If the submission does not exist, a new one will be created and returned.
	 * @param submissionList
	 * @param task
	 * @return
	 */
	private Submission findOrCreateSubmission(SubmissionList submissionList, Task task) {
		Submission.SubmissionPk id = new Submission.SubmissionPk();
		id.setSubmissionList(submissionList);
		id.setTask(task);
		Submission submission = service.submissionRepository.findOne(id);
		if (submission == null) {
			// Create it
			submission = new Submission();
			submission.setId(id);
			submission = service.submissionRepository.save(submission);
		}
		return submission;
	}

	/**
	 * Attempts to find the user by userId. If the user does not exist, they will be created with
	 * "Unknown" as name and ROLE_NONE as authority. A managed entity is returned, never null.
	 * @param userId
	 * @return
	 */
	private User findOrCreateUser(UserId userId) {
		User user = service.userRepository.findOneById(userId);
		if (user == null) {
			user = new User();
			user.setUserId(userId);
			user.setAuthority(UserAuthority.ROLE_NONE);
			user.setFullName("Unknown");
			user = service.userRepository.save(user);
		}
		return user;
	}

	/**
	 * Attempts to find the grouping by name. If the grouping does not exist, it will be created
	 * with no users. A managed entity is returned.
	 * @param name
	 * @return
	 */
	private Grouping findOrCreateGrouping(String name) {
		Grouping grouping = service.groupingRepository.findOneByGroupingName(name);
		if (grouping == null) {
			grouping = new Grouping();
			grouping.setUsers(new HashSet<>());
			grouping.setGroupingName(name);
			grouping = service.groupingRepository.save(grouping);
		}
		return grouping;
	}

	/**
	 * Attempts to find the group by name. If the group does not exist, it will be created with
	 * no users. A managed entity is returned.
	 * @param name
	 * @return
	 */
	private Group findOrCreateGroup(String name, Grouping grouping) {
		Group group = service.groupRepository.findOneByGroupNameAndGrouping(name, grouping);
		if (group == null) {
			group = new Group();
			group.setUsers(new HashSet<>());
			group.setGroupName(name);
			group.setGrouping(grouping);
			group = service.groupRepository.save(group);
		}
		return group;
	}

	/**
	 * Attempts to find the author by the given sheet. If the author does not exist it will be created. This method
	 * will never return null, but may throw a {@link CsvException} when the author sheet is invalid.
	 * @param sheet
	 * @return
	 */
	private Author findOrCreateAuthor(AuthorIdSheet sheet) throws CsvException {
		String groupingName = sheet.getGroupGroupingName();
		String groupName = sheet.getGroupName();
		UserId userId = sheet.getUserId();
		if (groupName != null && !groupName.isEmpty() && userId != null) {
			throw new CsvException("Ambiguous author data - both group and user id given");
		}
		if (userId != null) {
			return findOrCreateUser(userId);
		}
		if (groupName == null ^ groupingName == null) {
			throw new CsvException("Invalid author data - both grouping and group name need to be mentioned");
		}
		if (groupName != null && groupingName != null) {
			Grouping grouping = findOrCreateGrouping(groupingName);
			return findOrCreateGroup(groupName, grouping);
		}
		throw new CsvException("Missing author data");
	}

	/**
	 * Attempts to find the submission list. If none exist, it will create and persist a new one.
	 * @param author
	 * @param taskList
	 * @return
	 */
	private SubmissionList findOrCreateSubmissionList(Author author, TaskList taskList) {
		SubmissionList list =
				service.submissionListRepository.findOneByPrimaryKey(taskList.getTaskListId(), author.getAuthorId());
		if (list == null) {
			// Create a new one
			list = new SubmissionList();
			SubmissionList.SubmissionListPk id = new SubmissionList.SubmissionListPk();
			id.setAuthor(author);
			id.setTaskList(taskList);
			list.setId(id);
			list = service.submissionListRepository.save(list);
		}
		return list;
	}

	/**
	 * Utility method for reading files
	 * @param reader reader to use
	 * @param file file, or null. When null returns empty list
	 * @param <T> type of entity
	 * @return list of entities, never null
	 * @throws IOException
	 * @throws CsvException
	 */
	private <T> List<T> readMultipartFile(ObjectReader reader, MultipartFile file) throws IOException, CsvException {
		if (file == null)
			return Collections.emptyList();
		else {
			try {
				return reader.<T>readValues(file.getInputStream()).readAll();
			} catch (JsonProcessingException e) {
				String msg = "Could not parse CSV file: " + file.getName() + ", cause: " + e.getMessage();
				throw new CsvException(msg);
			}
		}
	}

	/**
	 * Utility method for reading a set of files where each file maps to one entry in the returned list.
	 * @param reader
	 * @param files
	 * @param <T>
	 * @return
	 * @throws IOException
	 * @throws CsvException
	 */
	private <T> List<T> readMultipartFileSet(ObjectReader reader, List<MultipartFile> files) throws IOException, CsvException {
		List<T> result = new LinkedList<>();
		for (MultipartFile file: files) {
			result.add(reader.readValue(file.getInputStream()));
		}
		return result;
	}

	/**
	 * Returns a map with as key the original file name, and as value the file.
	 * @param files
	 * @return
	 */
	private Map<String, MultipartFile> indexFiles(List<MultipartFile> files) {
		Map<String, MultipartFile> indexed = files.stream()
				.collect(Collectors.toMap(MultipartFile::getOriginalFilename, Function.identity()));
		if (indexed.size() != files.size()) {
			throw new CsvException("File names in a class need to be unique so they can be referenced from CSV.");
		}
		return indexed;
	}
}
