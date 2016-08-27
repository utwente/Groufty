package nl.javalon.groufty.resource.crud;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.review.instance.Review;
import nl.javalon.groufty.domain.task.Submission;
import nl.javalon.groufty.domain.task.SubmissionContentType;
import nl.javalon.groufty.domain.task.SubmissionList;
import nl.javalon.groufty.domain.task.Task;
import nl.javalon.groufty.domain.util.FileDetails;
import nl.javalon.groufty.dto.task.SubmissionDto;
import nl.javalon.groufty.repository.crud.*;
import nl.javalon.groufty.resource.BadRequestException;
import nl.javalon.groufty.resource.NotAcceptableException;
import nl.javalon.groufty.resource.UnauthorizedException;
import nl.javalon.groufty.security.GrouftyMethodSecurityExpressionRoot;
import nl.javalon.groufty.util.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import static nl.javalon.groufty.resource.RestResourceHelper.checkFound;

/**
 * Endpoint for submissions.
 *
 */
@Api(description = "Manage submissions")
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PREFIX + "submissions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SubmissionResource {

	@Inject
	private SubmissionRepository submissionRepository;

	@Inject
	private SubmissionListRepository submissionListRepository;
	
	@Inject 
	private UserRepository userRepository;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private TaskListRepository taskListRepository;

	@Inject
	private AuthorRepository authorRepository;

	@Inject
	private FileService fileService;

	@ApiOperation("Retrieve all submissions")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Page<Submission> getAll(Pageable pageable) {
		return submissionRepository.findAll(pageable);
	}

	@ApiOperation("Retrieve a single submission")
	@RequestMapping(value = "/{taskId}/{authorId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Submission get(
			@ApiParam(required = true) @PathVariable long taskId,
			@ApiParam(required = true) @PathVariable long authorId,
			GrouftyMethodSecurityExpressionRoot expressionRoot) {
		Submission submission = checkFound(submissionRepository.findOneByPrimaryKey(taskId, authorId));
		checkPermissions(expressionRoot, submission, false);
		return submission;
	}

	@ApiOperation("Retrieve a single submission file")
	@RequestMapping(value = "/{taskId}/{authorId}/file", method = RequestMethod.GET)
	public void getFile(
			@ApiParam(required = true) @PathVariable long taskId,
			@ApiParam(required = true) @PathVariable long authorId,
			HttpServletResponse response,
			GrouftyMethodSecurityExpressionRoot expressionRoot) throws SQLException, IOException {

		Submission submission = checkFound(submissionRepository.findOneByPrimaryKey(taskId, authorId));
		checkPermissions(expressionRoot, submission, false);

		// And send
		fileService.findFile(submission.getFileDetails(), response, false);
	}

	@ApiOperation("Retrieve a single submission file")
	@RequestMapping(value = "/by-review/{reviewId}/file", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public void getFileByReview(
			@ApiParam(required = true) @PathVariable long reviewId,
			HttpServletResponse response,
			GrouftyMethodSecurityExpressionRoot expressionRoot) throws SQLException, IOException {

		Submission submission = checkFound(submissionRepository.findByReviewId(reviewId));
		checkPermissions(expressionRoot, submission, true);

		// Only anonymous for students
		boolean anonymous = submission.getId().getSubmissionList().getTaskList().isAnonymousSubmissions() &&
				!expressionRoot.hasRole("ROLE_EDITOR");

		fileService.findFile(submission.getFileDetails(), response, anonymous);
	}


	@ApiOperation(value = "Update an existing submission", notes = "The lastEdited time will be computed server-side")
	@RequestMapping(value = "/{taskId}/{authorId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public void put(
			@ApiParam(required = true) @PathVariable long taskId,
			@ApiParam(required = true) @PathVariable long authorId,
			@ApiParam(required = true) @RequestBody SubmissionDto submissionDto,
			GrouftyMethodSecurityExpressionRoot expressionRoot) {

		Submission current = getOrCreateSubmission(taskId, authorId);
		checkPermissions(expressionRoot, current, false);
		checkSubmissionDeadline(current);
		updateSubmissionFromDto(submissionDto, current);
	}

	@ApiOperation(value = "Update a submissions file", notes = "The lastEdited time will be computed server-side")
	@RequestMapping(value = "/{taskId}/{authorId}/file", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void postFile(
			@ApiParam(required = true) @PathVariable long taskId,
			@ApiParam(required = true) @PathVariable long authorId,
			@ApiParam(required = true) @RequestPart MultipartFile multipartFile,
			GrouftyMethodSecurityExpressionRoot expressionRoot) throws IOException, SQLException {
		FileDetails details = fileService.convertFile(multipartFile);

		// Check if this submission allows files
		Task task = taskRepository.findOne(taskId);
		if (task.getContentType() == SubmissionContentType.TEXT_SUBMISSION) {
			throw new NotAcceptableException("File not allowed for this submission");
		}

		Submission current = getOrCreateSubmission(taskId, authorId);
		checkPermissions(expressionRoot, current, false);
		checkSubmissionDeadline(current);
		current.setFileDetails(details);
	}

	@ApiOperation("Deletes a submission. This will fail when there are reviews for the submission.")
	@RequestMapping(value = "/{taskId}/{authorId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void delete(
			@ApiParam(required = true) @PathVariable long taskId,
			@ApiParam(required = true) @PathVariable long authorId,
			GrouftyMethodSecurityExpressionRoot expressionRoot) {

		Submission submission = checkFound(submissionRepository.findOneByPrimaryKey(taskId, authorId));
		checkPermissions(expressionRoot, submission, false);
		checkSubmissionDeadline(submission);
		submissionRepository.delete(submission);
	}

	@ApiOperation("Deletes a submission file")
	@RequestMapping(value = "/{taskId}/{authorId}/file", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deleteFile(
			@ApiParam(required = true) @PathVariable long taskId,
			@ApiParam(required = true) @PathVariable long authorId,
			GrouftyMethodSecurityExpressionRoot expressionRoot) throws SQLException {

		Submission submission = checkFound(submissionRepository.findOneByPrimaryKey(taskId, authorId));
		checkPermissions(expressionRoot, submission, false);
		checkSubmissionDeadline(submission);
		submission.setFileDetails(null);
	}

	// Utility methods

	/**
	 * Updates the submission from the submission dto
	 * @param submissionDto
	 * @param submission
	 */
	private void updateSubmissionFromDto(SubmissionDto submissionDto, Submission submission) {
		SubmissionContentType submissionType = submission.getId().getTask().getContentType();
		if (submissionDto.getText() != null && submissionType != SubmissionContentType.TEXT_SUBMISSION) {
			throw new BadRequestException("Text not allowed for this submission");
		}
		submission.setText(submissionDto.getText());
		submission.setSubmitted(submissionDto.isSubmitted());
		if (submissionDto.getText() != null) {
			// Delete the file if there is one
			submission.setFileDetails(null);
		}
	}

	/**
	 * Finds or creates and persists a submission. The returned entity will always be managed.
	 * @param taskId
	 * @param authorId
	 * @return
	 */
	private Submission getOrCreateSubmission(long taskId, long authorId) {
		Submission submission = submissionRepository.findOneByPrimaryKey(taskId, authorId);
		if (submission == null) {

			submission = new Submission();

			// Create submission primary key
			Submission.SubmissionPk pk = new Submission.SubmissionPk();
			Task task = checkFound(taskRepository.findOne(taskId), "No such task");
			SubmissionList submissionList = submissionListRepository.findOneByPrimaryKey(task.getTaskListId(), authorId);

			// Create a new submission list if it does not exist
			if (submissionList == null) {
				SubmissionList.SubmissionListPk submissionListPk = new SubmissionList.SubmissionListPk();
				submissionListPk.setAuthor(checkFound(authorRepository.findOne(authorId), "No such author"));
				submissionListPk.setTaskList(task.getTaskList());

				submissionList = new SubmissionList();
				submissionList.setId(submissionListPk);
				submissionList = submissionListRepository.save(submissionList);
			}

			pk.setTask(task);
			pk.setSubmissionList(submissionList);

			submission.setId(pk);

			// And save
			submission = submissionRepository.save(submission);
		}
		return submission;
	}

	/**
	 * Checks permissions. If the currently authenticated user is either EDITOR or part of the submissions author group,
	 * the method will return. Otherwise, it will throw {@link UnauthorizedException}
	 * @param expressionRoot
	 * @param submission
	 * @param allowReviewers grant authors writing a review for this submission access
	 */
	private void checkPermissions(GrouftyMethodSecurityExpressionRoot expressionRoot, Submission submission, boolean allowReviewers) {
		if (expressionRoot.hasRole("ROLE_EDITOR"))
			return; // all permitted

		if (expressionRoot.hasRole("ROLE_PARTICIPANT")) {

			// Permit when the user is either inGroup of the submission
			if (expressionRoot.inGroup(submission.getAuthorId()))
				return;

			// or the user is inGroup of a review for the submission
			if (allowReviewers) {
				for (Review review: submission.getReviews()) {
					if (expressionRoot.inGroup(review.getAuthor()))
						return;
				}
			}
		}
		throw new UnauthorizedException();
	}

	/**
	 * Compares the current date with the deadline and throws an exception if the current date is past due.
	 * @param submission
	 */
	private void checkSubmissionDeadline(Submission submission) {
		Date now = new Date();
		Date submissionDeadline = submission.getId().getTask().getTaskList().getSubmissionDeadline();
		if (now.after(submissionDeadline))
			throw new NotAcceptableException("Past due submission modifications are not allowed");
	}
}
