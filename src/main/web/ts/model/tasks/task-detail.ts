import {TaskListExpand, StudentTaskListExpand} from "./task-list-expand";
import {Review} from "../reviews/review";
import {Audience} from "./task-list-overview";
import {ReviewSummary} from "../reviews/review-summary";

export class TaskDetail extends StudentTaskListExpand{
    public description : string;
    public startDate : number;
    public submissionDeadline : number;
    public submissionText : string;
    public reviewDeadline : number;
    public reviewTemplateId : number;
    public taskListName : string;
    public taskId : number;
    public authorId : number;
    public authorName : string;
    public submissionAudience : Audience;
    public reviews : ReviewSummary[]
}