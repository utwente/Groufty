import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {GrouftyHTTPService} from "./http.service";
import {AbstractService} from "./abstract.service";
import {ReviewTemplateOverview} from "../model/review-templates/review-template-overview";

/**
 * Service that provides review template overviews for the logged-in user.
 * @author Javalon Development Team
 * @version 0.1
 */
@Injectable()
export class ReviewTemplateOverviewService extends AbstractService {
    private _teacherReviewTemplateOverviewsUrl = GrouftyHTTPService.getAPI() + '/page/teacher/reviewtemplate-overview';
    private _teacherReviewTemplateDetailsUrl = GrouftyHTTPService.getAPI() + '/page/teacher/reviewtemplate-details';

    constructor (http: Http) {
        super(http);
    }

    /**
     * Get a list of review template overviews for the teacher perspective.
     * @param size The size of the requested page.
     * @param page The page to get
     * @returns {Observable<R>} The observable object for the request.
     */
    public getTeacherReviewTemplateOverviews(size : number = null, page : number = null) {
        let url : string = this._teacherReviewTemplateOverviewsUrl;
        return super.getPage<ReviewTemplateOverview>(url, size, page);
    }

    /**
     * Get a single review template overview for the editor.
     * @param id The ID of the template to get the overview for.
     * @returns {Observable<ReviewTemplateOverview>} The observable for the request.
     */
    public getTeacherReviewTemplateOverview(id : number) {
        let url : string = this._teacherReviewTemplateDetailsUrl + "/" + id;
        return super.performGet<ReviewTemplateOverview>(url);
    }
}