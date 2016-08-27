import {Observable} from "rxjs/Observable";
import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {ReviewDetail} from "../model/reviews/review-detail";
import {GrouftyHTTPService} from "./http.service";
import {AbstractService} from "./abstract.service";

@Injectable()
export class ReviewDetailService extends AbstractService{
    private _studentReviewDetailUrl = GrouftyHTTPService.getAPI() + '/page/review-details';
    
    constructor (http: Http) {
        super(http);
    }
    
    /**
     * Get a review-detail (submission).
     * @param reviewId The review for which to get details.
     * @param size The size of the requested page.
     * @param page The page to get
     * @returns {Observable<R>} The observable object for the request.
     */
    public getReviewDetails(reviewId : number, size : number = null) : Observable<ReviewDetail> {
        let url : string = this._studentReviewDetailUrl + '/' + reviewId;
        return super.performGet<ReviewDetail>(url);
    }
}