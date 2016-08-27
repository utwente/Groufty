import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {StudentTaskListOverview, TeacherTaskListOverview} from "../model/tasks/task-list-overview";
import {StudentTaskListExpand, TeacherTaskListExpand} from "../model/tasks/task-list-expand";
import {GrouftyHTTPService} from "./http.service";
import {AbstractService} from "./abstract.service";
import {Observable} from "rxjs/Observable";
import {Page} from "../model/utils/page";
import {TaskListDetail} from "../model/tasks/task-list-detail";
import {SubmissionListOverview} from "../model/submissions/submission-list-overview";
import {SubmissionListExpand} from "../model/submissions/submission-list-expand";

/**
 * Service that provides task-overviews and feedback-overviews for the logged-in user
 * @author Javalon Development Team
 * @version 0.1
 */
@Injectable()
export class TaskListService extends AbstractService {
	// Overviews URLs
	private _studentTaskListOverviewUrl = GrouftyHTTPService.getAPI() + '/page/student/tasklist-overview';
	private _studentFeedbackOverviewUrl = GrouftyHTTPService.getAPI() + '/page/student/feedback-overview';
	private _teacherTaskListOverviewUrl = GrouftyHTTPService.getAPI() + '/page/teacher/tasklist-overview';
	private _teacherSubmissionListOverviewUrl = GrouftyHTTPService.getAPI() + '/page/teacher/submissionlist-overview';
	
	// Detail URL
	private _teacherTaskListDetailUrl = GrouftyHTTPService.getAPI() + '/page/teacher/tasklist-details';

	// Expander URLs
	private _studentTaskListExpandUrl = GrouftyHTTPService.getAPI() + '/page/student/tasklist-expand';
	private _teacherTaskListExpandUrl = GrouftyHTTPService.getAPI() + '/page/teacher/tasklist-expand';
	private _teacherSubmissionListExpandUrl = GrouftyHTTPService.getAPI() + '/page/teacher/submissionlist-expand';


	constructor (http: Http) {
		super(http);
	}

	/**
	 * Get details for the given task list
	 * @param taskListId
	 * @returns {Observable<TaskListDetail>}
	 */
	public getTaskListDetail(taskListId : number) {
		let url = this._teacherTaskListDetailUrl + '/' + taskListId;
		return super.performGet<TaskListDetail>(url);
	}

    /**
     * Get a list of task overviews for student perspective (omitting task-overviews with startdates in the future).
     * @param size The size of the requested page.
     * @param page The page to get
     * @returns {Observable<R>} The observable object for the request.
     */
    public getStudentTaskOverviews(size : number = null, page : number = null) {
        return super.getPage<StudentTaskListOverview>(this._studentTaskListOverviewUrl, size, page);
    }
	
    /**
     * Get a list of feedback overviews for student perspective (omitting task-overviews with startdates in the future).
     * @param size The size of the requested page.
     * @param page The page to get
     * @returns {Observable<R>} The observable object for the request.
     */
    public getStudentFeedbackOverviews(size : number = null, page : number = null) {
        let url : string = this._studentFeedbackOverviewUrl;
        return super.getPage<StudentTaskListOverview>(url, size, page);
    }

	/**
	 * Get a list of task overviews for student perspective (omitting task-overviews with startdates in the future).
	 * @param size The size of the requested page.
	 * @param page The page to get
	 * @returns {Observable<R>} The observable object for the request.
	 */
	public getTeacherTaskOverviews(size : number = null, page : number = null) {
		let url : string = this._teacherTaskListOverviewUrl;
		return super.getPage<TeacherTaskListOverview>(url, size, page);
	}

	/**
	 * Get a list of submission list overviews for teacher perspective
	 * @param taskListId the task list for which to load submission lists
	 * @param diff Filter on review grade difference
	 * @param size The size of the requested page.
	 * @param page The page to get
	 * @returns {Observable<R>} The observable object for the request.
	 */
	public getTeacherSubmissionListOverviews(taskListId : number, diff : number, size : number = null, page : number = null) {
		let url = this._teacherSubmissionListOverviewUrl + '/' + taskListId + '?difference=';
		if (diff != null) {
			url = url + diff
		}
		return super.getPage<SubmissionListOverview>(url, size, page, true);
	}

	/**
	 * Get a list of tasks with some extra data for the given task list.
	 * @param taskListId The task list for which to get the expand.
	 * @param size The size of the requested page.
	 * @param page The page to get
	 * @returns {Observable<Page<StudentTaskListExpand>>} The observable object for the request.
	 */
	public getStudentTaskListExpand(taskListId : number, size : number = null, page : number = null) : Observable<Page<StudentTaskListExpand>> {
		let url : string = this._studentTaskListExpandUrl + '/' + taskListId;
		return super.getPage<StudentTaskListExpand>(url, size, page);
	}

	/**
	 * Get a list of tasks with some extra data for the given task list.
	 * @param taskListId
	 * @param size
	 * @param page
	 * @returns {Observable<Page<TeacherTaskListExpand>>}
	 */
	public getTeacherTaskListExpand(taskListId : number, size : number = null, page : number = null) : Observable<Page<TeacherTaskListExpand>> {
		let url : string = this._teacherTaskListExpandUrl + '/' + taskListId;
		return super.getPage<TeacherTaskListExpand>(url, size, page);
	}

	/**
	 * Get a list of submissions with some extra data for the given submission list.
	 * @param taskListId
	 * @param size
	 * @param page
	 * @returns {Observable<Page<SubmissionListExpand>>}
	 */
	public getTeacherSubmissionListExpand(taskListId : number, authorId : number, size : number = null, page : number = null) : Observable<Page<SubmissionListExpand>> {
		let url : string = this._teacherSubmissionListExpandUrl + '/' + taskListId + '/' + authorId;
		return super.getPage<SubmissionListExpand>(url, size, page);
	}


}