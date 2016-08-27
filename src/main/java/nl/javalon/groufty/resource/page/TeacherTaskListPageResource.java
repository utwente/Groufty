package nl.javalon.groufty.resource.page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.javalon.groufty.config.RestPrefixConfiguration;
import nl.javalon.groufty.dto.page.task.TeacherTaskListExpandDto;
import nl.javalon.groufty.dto.page.task.TeacherTaskListOverviewDto;
import nl.javalon.groufty.repository.page.TeacherTaskListPageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Everything "All Tasks".
 * @author Lukas Miedema
 */
@Transactional
@RestController
@RequestMapping(value = RestPrefixConfiguration.PAGE_PREFIX + "teacher")
@Api("Everything 'All Tasks'")
public class TeacherTaskListPageResource {

	@Inject	private TeacherTaskListPageRepository teacherTaskListPageRepository;

	@ApiOperation("Get all task lists in the system with relevant information")
	@RequestMapping(value = "tasklist-overview", method = RequestMethod.GET)
	public Page<TeacherTaskListOverviewDto> getTaskListOverview(Pageable page) {
		return teacherTaskListPageRepository.getTeacherTaskListOverview(page);
	}

	@ApiOperation("Get info about one task list")
	@RequestMapping(value = "tasklist-details/{taskListId}", method = RequestMethod.GET)
	public TeacherTaskListOverviewDto getTaskListOverview(@PathVariable long taskListId) {
		return teacherTaskListPageRepository.getTeacherTaskListDetails(taskListId);
	}

	@ApiOperation("Get all tasks for a certain task list with relevant information")
	@RequestMapping(value = "tasklist-expand/{taskListId}", method = RequestMethod.GET)
	public Page<TeacherTaskListExpandDto> getTaskListExpand(@PathVariable long taskListId, Pageable page) {
		return teacherTaskListPageRepository.getTeacherTaskListExpand(taskListId, page);
	}
}