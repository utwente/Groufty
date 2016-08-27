import {Injectable} from "@angular/core";
import {Http, Headers, RequestOptions} from "@angular/http";
import {GrouftyHTTPService} from "./http.service";
import {Review} from "../model/reviews/review";
import {ReviewDTO} from "../model/reviews/review-dto";
import {Flag} from "../model/reviews/flag";
import {AbstractService} from "./abstract.service";

@Injectable()
export class ReviewService extends AbstractService{
	private _studentReviewUrl = GrouftyHTTPService.getAPI() + '/reviews';

	constructor (http: Http) {
		super(http);
	}
	
    /**
     * Get a review template.
     * @returns {Observable<R>} The observable object for the request.
     */
    public getReview(reviewId : number) {
        let url : string = this._studentReviewUrl + '/' + reviewId;
        return super.performGet<Review>(url);
    }

    /**
     * Update the specified review.
     * @param review The review to update.
     * @returns {Observable<Response>} The observable object for the request.
     */
    public updateReview(review : Review) {
		let body = JSON.stringify(ReviewDTO.dtoFromReview(review));
        let url = this._studentReviewUrl + '/' + review.reviewId;
		return super.performUpdate<Review>(url, body);
    }


    /**
     * Flag the specified review.
     * @param reviewId The review Id
     * @param flag The flag object
     * @returns {Observable<Response>} The observable object for the request.
     */
    public flagReview(reviewId : number, flag : Flag) {
        let body = JSON.stringify(flag);
        let headers = new Headers({ 'Content-Type': 'application/json'});
        let options = new RequestOptions({ headers: headers});
        return super.getHttp().put(this._studentReviewUrl + '/' + reviewId + '/flag', body, options)
            .catch(super.handleError)
    }

}