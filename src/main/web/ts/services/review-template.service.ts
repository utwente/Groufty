import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {ReviewTemplate} from "../model/review-templates/review-template";
import {GrouftyHTTPService} from "./http.service";
import {AbstractService} from "./abstract.service";

@Injectable()
export class ReviewTemplateService extends AbstractService{
    private _reviewTemplateUrl = GrouftyHTTPService.getAPI() + '/reviewtemplates';

    constructor (http: Http) {
		super(http);
	}

    /**
     * Get a review template.
     * @returns {Observable<R>} The observable object for the request.
     */
    public getReviewTemplate(reviewTemplateId : number) {
        let url : string = this._reviewTemplateUrl + '/' + reviewTemplateId;
        return super.performGet<ReviewTemplate>(url);
    }

    /**
     * Create a new review template.
     * @param reviewTemplate The review template to POST.
     * @returns {Observable<ReviewTemplate>} The review template got back from the request.
     */
    public createReviewTemplate(reviewTemplate : ReviewTemplate) {
        let url : string = this._reviewTemplateUrl;
        let body : string = JSON.stringify(reviewTemplate);
        return super.performCreate<ReviewTemplate>(url, body);
    }

    /**
     * Update the review template.
     * @param reviewTemplate The review template that should be updated.
     * @returns {Observable<ReviewTemplate>} The review template that got updated.
     */
    public updateReviewTemplate(reviewTemplate : ReviewTemplate) {
        let url : string = this._reviewTemplateUrl + '/' + reviewTemplate.reviewTemplateId;
        let body : string = JSON.stringify(reviewTemplate);
        return super.performUpdate<ReviewTemplate>(url, body);
    }

    /**
     * Delete the review template.
     * @param reviewTemplateId The id of the review template that should be deleted.
     * @returns {Observable<T>} The response of the delete request.
     */
    public deleteReviewTemplate(reviewTemplateId : number) {
        let url : string = this._reviewTemplateUrl + '/' + reviewTemplateId;
        return super.performDelete(url);
    }

}