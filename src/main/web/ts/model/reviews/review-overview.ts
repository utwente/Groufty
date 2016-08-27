import {Audience} from "../tasks/task-list-overview";

export class ReviewOverview {
    public taskListId : number;
    public taskId : number;
    public taskName: string;
    public reviewDeadline : number;
    public taskTarget : Audience;
    public anonymousReview : boolean;
    public totalReviewCount : number;
    public submittedReviewCount : number;
    public showGradesToReviewers : boolean;
    public lastEdited : number;
}