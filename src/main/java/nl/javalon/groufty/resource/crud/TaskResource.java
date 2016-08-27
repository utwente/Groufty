package nl.javalon.groufty.resource.crud;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.domain.task.Task;
import nl.javalon.groufty.domain.task.TaskList;
import nl.javalon.groufty.domain.user.User;
import nl.javalon.groufty.dto.task.TaskDto;
import nl.javalon.groufty.repository.crud.ReviewTemplateRepository;
import nl.javalon.groufty.repository.crud.TaskListRepository;
import nl.javalon.groufty.repository.crud.TaskRepository;
import nl.javalon.groufty.repository.crud.UserRepository;
import nl.javalon.groufty.resource.UnauthorizedException;
import nl.javalon.groufty.security.GrouftyMethodSecurityExpressionRoot;
import nl.javalon.groufty.util.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;

import static nl.javalon.groufty.resource.RestResourceHelper.checkFound;

/**
 * Endpoint for tasks
 */
@Slf4j
@Api(description = "Manage tasks")
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PREFIX + "tasks", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskResource {

	@Inject
	private TaskRepository taskRepository;
	
	@Inject
	private TaskListRepository taskListRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private ReviewTemplateRepository reviewTemplateRepository;

	@Inject
	private FileService fileService;

	@ApiOperation("Retrieve all tasks, paginated")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Page<Task> getAll(Pageable pageable) {
		return taskRepository.findAll(pageable);
	}

	@ApiOperation("Retrieve tasks by tasklist, paginated")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(value = "by-tasklist/{taskListId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Page<Task> getAllByTaskList(
			@ApiParam(required = true) @PathVariable long taskListId,
			Pageable pageable) {
		TaskList taskList = checkFound(taskListRepository.findOne(taskListId));
		return taskRepository.findByTaskList(taskList, pageable);
	}

	@ApiOperation("Retrieve a single task")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Task get(@ApiParam(required = true) @PathVariable long taskId) {
		return checkFound(taskRepository.findOne(taskId));
	}

	@ApiOperation("Retrieve a single task file")
	@RequestMapping(value = "/{taskId}/file", method = RequestMethod.GET)
	public void getFile(
			@ApiParam(required = true) @PathVariable long taskId,
			HttpServletResponse response,
			GrouftyMethodSecurityExpressionRoot expressionRoot) throws SQLException, IOException {

		Task task = checkFound(taskRepository.findOne(taskId));

		// Check security
		if (expressionRoot.hasRole("PARTICIPANT")) {
			if (!expressionRoot.inGrouping(task.getTaskList().getGroupingId()))
				throw new UnauthorizedException();
		} else if (!expressionRoot.hasRole("EDITOR"))
			throw new UnauthorizedException();

		fileService.findFile(task.getFileDetails(), response, false);
	}

	@ApiOperation(value = "Create a new task", notes = "Assigned task id will be in the returned instance. " +
			"Authenticated user will become author.")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public Task post(@ApiParam(required = true) @RequestBody @Valid TaskDto taskDto, Authentication authentication) {

		// Convert the task
		Task task = new Task();
		updateTaskFromDto(taskDto, task, authentication);

		// And persist
		return taskRepository.save(task);
	}

	@ApiOperation(value = "Update an existing task. Authenticated user will become author.")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(value = "/{taskId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Task put(
			@ApiParam(required = true) @PathVariable long taskId,
			@ApiParam(required = true) @RequestBody @Valid TaskDto taskDto,
			Authentication authentication) {

		Task current = checkFound(taskRepository.findOne(taskId));
		updateTaskFromDto(taskDto, current, authentication);
		return current;
	}

	@ApiOperation(value = "Store a single task file", notes = "Requires the task to already exist")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(value = "/{taskId}/file", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void postTaskFile(
			@ApiParam(required = true) @PathVariable long taskId,
			@ApiParam(required = true) @RequestPart MultipartFile multipartFile
	) throws IOException, SQLException {
		Task task = checkFound(taskRepository.findOne(taskId));
		task.setFileDetails(fileService.convertFile(multipartFile));
	}

	@ApiOperation(value = "Delete a task", notes = "this will also delete all related submissions and reviews")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(value = "/{taskId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@ApiParam(required = true) @PathVariable long taskId) {
		taskRepository.delete(taskId);
	}

	@ApiOperation(value = "Delete a task file")
	@PreAuthorize("hasRole('ROLE_EDITOR')")
	@RequestMapping(value = "/{taskId}/file", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTaskFile(
			@ApiParam(required = true) @PathVariable long taskId) throws IOException {
		Task task = checkFound(taskRepository.findOne(taskId), "No such task");
		task.setFileDetails(null);
	}

	// Utility methods
	private void updateTaskFromDto(TaskDto dto, Task task, Authentication authentication) {
		task.setName(dto.getName());
		task.setDescription(dto.getDescription());
		task.setContentType(dto.getContentType());
		task.setTaskList(checkFound(taskListRepository.findOne(dto.getTaskListId()), "No such tasklist"));
		User user = (User) authentication.getPrincipal();
		task.setAuthor(user);
	}
}
