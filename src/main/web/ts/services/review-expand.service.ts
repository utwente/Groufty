import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {ReviewExpand} from "../model/reviews/review-expand";
import {GrouftyHTTPService} from "./http.service";
import {AbstractService} from "./abstract.service";


/**
 * Service that provides task-overviews for the logged-in user
 * @author Javalon Development Team
 * @version 0.1
 */
@Injectable()
export class ReviewExpandService extends AbstractService{
    private reviewExpandUrl = GrouftyHTTPService.getAPI() + '/page/review-expand';

    constructor (http: Http) {
        super(http);
    }

    /**
     * Get a list of reviews
     * @param taskId The task for which to get the expand.
     * @param size The size of the requested page.
     * @param page The page to get
     * @returns {Observable<R>} The observable object for the request.
     */
    public getReviewExpand(taskId : number, size : number = null, page : number = null) {
        let url : string = this.reviewExpandUrl + '/' + taskId;
        return super.getPage<ReviewExpand>(url, size, page);
    }
}