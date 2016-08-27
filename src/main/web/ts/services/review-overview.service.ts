import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {ReviewOverview} from "../model/reviews/review-overview";
import {GrouftyHTTPService} from "./http.service";
import {AbstractService} from "./abstract.service";

/**
 * Service that provides review-overviews for the logged-in user
 * @author Javalon Development Team
 * @version 0.1
 */
@Injectable()
export class ReviewOverviewService extends AbstractService{
    private _studentReviewOverviewUrl = GrouftyHTTPService.getAPI() + '/page/review-overview';

    constructor (http: Http) {
		super(http);
	}

    /**
     * Get a list of review overviews for student perspective.
     * @param size The size of the requested page.
     * @param page The page to get
     * @returns {Observable<R>} The observable object for the request.
     */
    public getStudentReviewOverviews(size : number = null, page : number = null) {
        let url : string = this._studentReviewOverviewUrl;
        return super.getPage<ReviewOverview>(url, size, page);
    }
}