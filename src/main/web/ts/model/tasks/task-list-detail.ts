import {TeacherTaskListState, Audience} from "./task-list-overview";

export class TaskListDetail {
	public taskListId : number;
	public taskListName : string;
	public submissionAudience : Audience;
	public startDate : number;
	public submissionDeadline : number;
	public reviewDeadline : number;
	public state : TeacherTaskListState;

	public authorName : string; // Submission author's name
	public authorUserId : string;

	public groupingId : number; // Grouping to which task list was assigned
	public groupingName : string; // Name of the grouping
	public submitterCount : number;

	public taskCount : number; // number of tasks in the task list
	public submittedSubmissionsCount : number; // number of submitted tasks for this task list

	public reviewerCount : number; // number of reviewers assigned by review selection strategy
	public submittedReviewsCount : number; // number of submitted reviews

	public anonymousReviews : boolean; 
	public anonymousSubmissions : boolean;
}