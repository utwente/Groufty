package nl.javalon.groufty.resource.crud;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.csv.CsvWriter;
import nl.javalon.groufty.csv.TaskListWriter;
import nl.javalon.groufty.domain.task.TaskList;
import nl.javalon.groufty.domain.user.Grouping;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.domain.user.UserId;
import nl.javalon.groufty.dto.task.TaskListDto;
import nl.javalon.groufty.repository.crud.GroupingRepository;
import nl.javalon.groufty.repository.crud.TaskListRepository;
import nl.javalon.groufty.repository.crud.UserRepository;
import nl.javalon.groufty.resource.RestResourceHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static nl.javalon.groufty.resource.RestResourceHelper.checkFound;

/**
 * Endpoint for managing task lists.
 */
@Api(description = "Manage tasklists")
@PreAuthorize("hasRole('ROLE_EDITOR')")
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PREFIX + "tasklists", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskListResource {

	@Inject
	private TaskListRepository taskListRepository;
	
	@Inject
	private UserRepository userRepository;

	@Inject
	private GroupingRepository groupingRepository;

	@Inject
	private TaskListWriter taskListWriter;

	@ApiOperation("Retrieve all tasklists, paginated")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Page<TaskList> getAll(Pageable pageable) {
		return taskListRepository.findAll(pageable);
	}

	@ApiOperation("Retrieve one tasklist")
	@RequestMapping(value = "/{taskListId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public TaskList getOne(	@ApiParam(required = true) @PathVariable long taskListId) {
		return checkFound(taskListRepository.findOne(taskListId));
	}

	@ApiOperation(value = "Create a new tasklist", notes = "Returned tasklist will contain the new taskListId")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public TaskList post(@ApiParam(required = true) @RequestBody @Valid TaskListDto taskListDto, Authentication auth) {
		// Convert
		TaskList taskList = new TaskList();
		updateTaskListFromDto((User) auth.getPrincipal(), taskListDto, taskList);

		// Save
		return taskListRepository.save(taskList);
	}

	@ApiOperation("Update an existing tasklist")
	@RequestMapping(value = "/{taskListId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public TaskList put(
			@ApiParam(required = true) @PathVariable long taskListId,
			@ApiParam(required = true) @RequestBody @Valid TaskListDto taskListDto,
			Authentication auth) {
		TaskList current = checkFound(taskListRepository.findOne(taskListId));
		updateTaskListFromDto((User) auth.getPrincipal(), taskListDto, current);
		return current;
	}

	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@ApiOperation(value = "Delete an existing tasklist", notes = "Will also delete any tasks in the tasklist")
	@RequestMapping(value = "/{taskListId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@ApiParam(required = true) @PathVariable long taskListId) {
		taskListRepository.delete(taskListId);
	}

	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@ApiOperation(value = "Retrieve tasklists by author, paginated", notes = "the author must be an employee")
	@RequestMapping(value = "by-user/{userId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Page<TaskList> getAllByUser(
			@ApiParam(required = true) @PathVariable UserId userId, Pageable pageable) {

		RestResourceHelper.checkNotNull(userId);
		User user = userRepository.findOneById(userId);
		checkFound(user);
		return taskListRepository.findByAuthor(user, pageable);
	}

	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@ApiOperation("Export all submission lists and reviews from the provided task list")
	@RequestMapping(value = "/{taskListId}/export", method = RequestMethod.GET)
	public void exportTaskList(
			@ApiParam(required = true) @PathVariable long taskListId,
	        HttpServletResponse response) throws IOException {

		TaskList taskList = checkFound(this.taskListRepository.findOne(taskListId));

		String fileName = CsvWriter.escapeFileName(taskList.getName() + " Export") + ".zip";
		response.setHeader("content-disposition", "attachment; filename=" + fileName);
		response.setContentType("application/zip");

		// Lets write
		taskListWriter.exportTaskList(taskList, response.getOutputStream());
	}

	// Utility methods
	private void updateTaskListFromDto(User user, TaskListDto taskListDto, TaskList taskList) {
		taskList.setAuthor(user);

		taskList.setSubmissionDeadline(taskListDto.getSubmissionDeadline());
		taskList.setStartDate(taskListDto.getStartDate());
		taskList.setReviewDeadline(taskListDto.getReviewDeadline());

		taskList.setState(taskListDto.getState());
		taskList.setName(taskListDto.getName());
		taskList.setSubmissionAudience(taskListDto.getSubmissionAudience());
		taskList.setReviewerSelectionStrategy(taskListDto.getReviewerSelectionStrategy());

		Long groupingId = taskListDto.getGroupingId();
		if (groupingId != null) {
			Grouping grouping = checkFound(groupingRepository.findOne(groupingId), "No such grouping");
			taskList.setGrouping(grouping);
		}
	}
}