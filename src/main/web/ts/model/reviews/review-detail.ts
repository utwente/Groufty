import {ContentType} from "../tasks/task";
import {Audience} from "../tasks/task-list-overview";

export class ReviewDetail {
    public anonymousSubmitter : boolean;
    public anonymousSubmissions : boolean;
    public grade : number;
    public lastEdited : number;
    public reviewDeadline : number;
    public reviewId : number;
    public reviewTemplateId : number;
    public reviewAudience : Audience;
    public reviewerName : string;
    public submissionFile : string;
    public submissionText : string;
    public submissionAudience : Audience;
    public submitted : boolean;
    public submitterName : string;
    public taskDescription : string;
    public taskFileName : string;
    public taskName: string;
    public taskId : number;
    public showGradesToReviewers : boolean;
	public submissionContentType: ContentType;
    
}