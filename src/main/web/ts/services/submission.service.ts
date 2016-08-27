import {Injectable} from "@angular/core";
import {Http, Headers, RequestOptions} from "@angular/http";
import {Task} from "../model/tasks/task";
import {Submission} from "../model/submissions/submission";
import {GrouftyHTTPService} from "./http.service";
import {AbstractService} from "./abstract.service";

/**
 * Service that provides the submission service. Note that there is no method to add a submission, because submissions always exist in the system.
 * @author Javalon Development Team
 * @version 0.1
 */
@Injectable()
export class SubmissionService extends AbstractService {
	private _submissionUrl = GrouftyHTTPService.getAPI() + '/submissions';

	constructor (http: Http) {
        super(http);
    }
	
    /**
     * Get a list of submissions.
     * @param size The size of the requested page.
     * @param page The page to get
     * @returns {Observable<R>} The observable object for the request.
     */
    public getSubmissions(size : number = null, page : number = null) {
        return super.getPage<Task>(this._submissionUrl, size, page);
    }

    /**
     * Get the submission for the given task by the given author
     * @param taskId The task for which to get the submission
     * @param authorId The author which created the submission
     * @returns {Observable<R>} The observable object for the request.
     */
    public getSubmission(taskId : number, authorId : number) {
        let url = this._submissionUrl + '/' + taskId + '/' + authorId;
        return super.performGet<Task>(url);
    }

    /**
     * Update the specified submission
     * @param submission The submission to update.
     * @param taskId 
     * @param authorId
     * @returns {Observable<Response>} The observable object for the request.
     */
    public updateSubmission(submission : Submission, taskId : number, authorId : number) {
        let body = JSON.stringify(submission);
        let headers = new Headers({ 'Content-Type': 'application/json'});
        let options = new RequestOptions({ headers: headers});
        return super.getHttp().put(this._submissionUrl + '/' + taskId + '/' + authorId, body, options)
            .catch(super.handleError)
    }

    /**
     * Delete the specified submission.
     * @param taskId
     * @param authorId
     * @returns {Observable<Response>} The observable object for the request.
     */
    public deleteSubmission(taskId : number, authorId : number){
        return super.getHttp().delete(this._submissionUrl + '/' + taskId + '/' + authorId)
            .catch(super.handleError)
    }
}