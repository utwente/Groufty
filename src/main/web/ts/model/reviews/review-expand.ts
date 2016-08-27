import {Audience} from "../tasks/task-list-overview";

export class ReviewExpand {
    public anonymousReviews : boolean;
    public anonymousSubmissions : boolean;
    public grade : number;
    public lastEdited : number;
    public reviewId : number;
    public reviewAudience : Audience;
    public reviewerName : string;
    public submissionAudience : Audience;
    public submitted : boolean;
    public submitterName : string;
    public taskDescription : string;
}