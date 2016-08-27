import {Injectable} from "@angular/core";
import {Http, Headers, RequestOptions} from "@angular/http";
import {TaskDetail} from "../model/tasks/task-detail";
import {GrouftyHTTPService} from "./http.service";
import {AbstractService} from "./abstract.service";


/**
 * Service that provides task-detail for the given task in a task list
 * @author Javalon Development Team
 * @version 0.1
 */
@Injectable()
export class TaskDetailService extends AbstractService{
	private _studentTaskDetailUrl = GrouftyHTTPService.getAPI() + '/page/student/task-details';
    private _teacherTaskDetailUrl = GrouftyHTTPService.getAPI() + '/page/teacher/submission-details';


    constructor (http: Http) {
        super(http);
    }

    /**
     * Get a task-detail (submission).
     * @param taskId The task for which to get details.
     * @param size The size of the requested page.
     * @param page The page to get
     * @returns {Observable<R>} The observable object for the request.
     */
    public getStudentTaskDetails(taskId : number, size : number = null, page : number = null) {
        let url : string = this._studentTaskDetailUrl + '/' + taskId;
        return super.performGet<TaskDetail>(url);
    }

    /**
     * Get a task-detail (submission).
     * @param taskId The task for which to get details.
     * @param size The size of the requested page.
     * @param page The page to get
     * @returns {Observable<R>} The observable object for the request.
     */
    public getTeacherTaskDetails(taskId : number, authorId : number, size : number = null, page : number = null) {
        let url : string = this._teacherTaskDetailUrl + '/' + taskId + '/' + authorId;
        return super.performGet<TaskDetail>(url);
    }

    /**
     * Update the task details
     * @param taskDetail The task-detail to update
     * @returns {Observable<Response>}
     */
    public updateTaskDetails(taskDetail : TaskDetail) {
        let body = JSON.stringify(taskDetail);
        let headers = new Headers({ 'Content-Type': 'application/json'});
        let options = new RequestOptions({ headers: headers});
        return super.getHttp().put(this._studentTaskDetailUrl + '/' + taskDetail.taskId + '/' + taskDetail.authorId, body, options)
            .catch(super.handleError)
    }
    


}